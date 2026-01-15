package br.com.iatapp;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.TestPropertySource;
import br.com.iatapp.repositories.ConfigSwitchIdRepository;
import br.com.iatapp.repositories.ConfigSwitchLogRepository;
import br.com.iatapp.repositories.ConfigSwitchResultadosRepository;
import br.com.iatapp.repositories.ConfigSwitchScriptRepository;
import br.com.iatapp.repositories.Vivo2IdRepository;
import br.com.iatapp.repositories.Vivo2ResultadosRepository;
import br.com.iatapp.repositories.Vivo2ScriptRepository;
import br.com.iatapp.repositories.ProcessoIdVivo2Repository;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = {
		"spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,"
				+ "org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration,"
				+ "org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration,"
				+ "org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration",
		"spring.data.mongodb.repositories.enabled=false"
})
@TestPropertySource(properties = {
		"environment=DEV",
		"jumpserver.galvao.ip=127.0.0.1",
		"jumpserver.galvao.user=user",
		"jumpserver.galvao.password=pass",
		"jumpserver.tatuape.ip=127.0.0.1",
		"jumpserver.tatuape.user=user",
		"jumpserver.tatuape.password=pass",
		"jumpserver.gesa.ip=127.0.0.1",
		"mysql.host=localhost",
		"mysql.database=test",
		"iat.url=http://localhost",
		"scripts.path=/tmp",
		"fourtyone.url=http://localhost",
		"fourtyone.user=user",
		"fourtyone.password=pass",
		"SWITCH_LOG_PATH=/tmp",
		"API_SERVICO_ENDPOINT=http://localhost",
		"SIP_ONE_CORE_LOG_PATH=/tmp"
})
public class OttapStaterTemplateApplicationTests {

	@MockBean
	private MongoTemplate mongoTemplate;
	@MockBean
	private ConfigSwitchIdRepository configSwitchIdRepository;
	@MockBean
	private ConfigSwitchLogRepository configSwitchLogRepository;
	@MockBean
	private ConfigSwitchResultadosRepository configSwitchResultadosRepository;
	@MockBean
	private ConfigSwitchScriptRepository configSwitchScriptRepository;
	@MockBean
	private Vivo2IdRepository vivo2IdRepository;
	@MockBean
	private Vivo2ResultadosRepository vivo2ResultadosRepository;
	@MockBean
	private Vivo2ScriptRepository vivo2ScriptRepository;
	@MockBean
	private ProcessoIdVivo2Repository processoIdVivo2Repository;

	@Test
	public void contextLoads() {
	}

}
