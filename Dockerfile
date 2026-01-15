FROM docker.io/tomcat:9.0.8-jre8-alpine
# Para apagar o /webapps padrão do tomcat
RUN rm -rf /usr/local/tomcat/webapps/
# Para importar o .war da aplicação
COPY target/vivo-b2b-configuracao.war /usr/local/tomcat/webapps/ROOT.war
EXPOSE 8080