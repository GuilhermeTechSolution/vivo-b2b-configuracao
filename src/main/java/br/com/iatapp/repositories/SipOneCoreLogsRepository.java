package br.com.iatapp.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import br.com.iatapp.domain.SipOneCoreLogsDomain;


public interface SipOneCoreLogsRepository extends MongoRepository<SipOneCoreLogsDomain, String> {

	SipOneCoreLogsDomain findOneByIdTeste(long idTeste);
}
