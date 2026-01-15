package br.com.iatapp.api;

import br.com.iatapp.dao.SenhasDao;
import br.com.iatapp.dao.VivoB2BDao;
import br.com.iatapp.domain.Vivo2IdDomain;
import br.com.iatapp.helper.StringHelper;
import br.com.iatapp.model.UsuarioModel;
import br.com.iatapp.repositories.*;
import br.com.iatapp.threads.ThreadAtivacaoVivo2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping(value="/api/vivo2")
public class Vivo2Resource {

    @Autowired
    private Vivo2IdRepository vivo2IdRepository;

    @Autowired
    private Vivo2ResultadosRepository vivo2ResultadosRepository;

    @Autowired
    private Vivo2ScriptRepository vivo2ScriptRepository;

    @Autowired
    private ProcessoIdVivo2Repository processoIdVivo2Repository;

    @PostMapping(value = "/teste-connector", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> executarTeste(
            @RequestHeader("name") String name,
            @RequestBody Vivo2IdDomain vivo2IdDomain
    ) {
        try {
            // Validando os campos obrigatorios
            if(vivo2IdDomain == null ||
                    StringUtils.isBlank(vivo2IdDomain.getIdTbs()) ||
                    StringUtils.isBlank(vivo2IdDomain.getHostname()) ||
                    StringUtils.isBlank(vivo2IdDomain.getCliente()) ||
                    StringUtils.isBlank(vivo2IdDomain.getIpWan())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("campos_obrigatorios_vazios");
            }

            // verificando o hostname
            if(StringUtils.containsIgnoreCase(vivo2IdDomain.getHostname(), "rdist") || StringUtils.containsIgnoreCase(vivo2IdDomain.getHostname(), "RCNG")) {

                // find Backbone
                try {
                    vivo2IdDomain.setRdist(vivo2IdDomain.getHostname().toLowerCase());
                    String backbone = new VivoB2BDao().findBackboneByRdist(vivo2IdDomain.getHostname());
                    if(StringUtils.isBlank(backbone)) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR ).body("equipamento_nao_encontrado");
                    }


                    vivo2IdDomain.setBackbone(backbone);

                } catch (Exception e) {
                    e.printStackTrace();
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("equipamento_nao_encontrado");
                }

            } else {

                // find rdist
                try {
                    vivo2IdDomain.setBackbone(vivo2IdDomain.getHostname().toLowerCase());
                    String rdist = new VivoB2BDao().findRdistByBackbone(vivo2IdDomain.getHostname());

                    vivo2IdDomain.setRdist(rdist);

                } catch (Exception e) {
                    e.printStackTrace();
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("equipamento_nao_encontrado");
                }
            }

            // tratando o nome do cliente
            vivo2IdDomain.setCliente(getNomeCliente(vivo2IdDomain.getCliente()));

            // setando o usuario do teste
            vivo2IdDomain.setNomeUsuario(name);
            vivo2IdDomain.setIdUsuario(2600); //Usuário sistêmico vanUser

            if(vivo2IdDomain.getRegistryChangedByUser()){
                String user = StringUtils.substringAfterLast(vivo2IdDomain.getNomeUsuario(), "-").trim();
                vivo2IdDomain.setUserChangedRegistration(user);
                vivo2IdDomain.setDateChangedRegistration(new Date());
            }

            // Buscando as sehas de CPE, RA, Rede IP
            UsuarioModel usuarioSenhas = null;
            try {
                usuarioSenhas = new SenhasDao().buscarSenhasUsuarioIaTConfMaster();
            } catch (Exception e) {
                usuarioSenhas = null;
                e.printStackTrace();
            }

            if(usuarioSenhas == null ||
                    StringUtils.isBlank(usuarioSenhas.getLoginPe()) ||
                    StringUtils.isBlank(usuarioSenhas.getSenhaPe()) ||
                    StringUtils.isBlank(usuarioSenhas.getLoginRedeIp()) ||
                    StringUtils.isBlank(usuarioSenhas.getSenhaRedeIp())) {
                // Nao possui senhas cadastradas
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("sem_senhas_cadastradas");
            }

            vivo2IdDomain.setUsuarioSenhas(usuarioSenhas);
            vivo2IdDomain.setDataInicio(new Date());

            // salvando no MongoDB
            this.vivo2IdRepository.save(vivo2IdDomain);

            // Iniciando a Thread do Teste
            ThreadAtivacaoVivo2 threadAtivacao = new ThreadAtivacaoVivo2(
                    null,
                    vivo2IdDomain,
                    this.vivo2IdRepository,
                    this.vivo2ResultadosRepository,
                    this.vivo2ScriptRepository
            );
            threadAtivacao.start();

            return ResponseEntity.status(HttpStatus.OK).body(vivo2IdDomain.getId());
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error");
        }
    }

    private String getNomeCliente(String nomeCliente) {
        nomeCliente = nomeCliente.toUpperCase();
        String cliente = StringHelper.removeCaracteresEspeciais(nomeCliente);
        String[] clienteArray = cliente.split(" ");

        return clienteArray[0];
    }
}
