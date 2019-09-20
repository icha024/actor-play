package com.clianz.kube.actors;

import com.clianz.kube.actorsystem.BaseActor;
import com.clianz.kube.actorsystem.Event;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.transport.JschConfigSessionFactory;
import org.eclipse.jgit.transport.OpenSshConfig;
import org.eclipse.jgit.transport.SshSessionFactory;
import org.eclipse.jgit.transport.SshTransport;
import org.eclipse.jgit.util.FS;

import java.io.File;

@Slf4j
public class ListenerActor extends BaseActor {

    @Override
    protected void init() {
        log.info("Getting GIT..........");
        try {
            SshSessionFactory sshSessionFactory = new JschConfigSessionFactory() {
                @Override
                protected void configure(OpenSshConfig.Host hc, Session session) {
                    // Do nothing
                }

                @Override
                protected void configureJSch(JSch jsch) {
                    super.configureJSch(jsch);
                    try {
                        jsch.addIdentity("/tmp/kube-dep-sync_id_rsa");
                        log.info("CONFIGURED PRIVATE KEY.....");
                    } catch (JSchException e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    }
                }
            };

            Git git = Git.cloneRepository()
                         .setTransportConfigCallback(transport -> {
                             SshTransport sshTransport = (SshTransport) transport;
                             sshTransport.setSshSessionFactory(sshSessionFactory);
                         })
                         .setURI("ssh://git@github.com/icha024/kube-deploy-sync.git")
                         .setDirectory(new File("/tmp/newRepo"))
                         .call();


//            FileRepositoryBuilder builder = new FileRepositoryBuilder();
//            Repository repository = builder.setGitDir(new File("/home/ian/git/kube-deploy-sync/.git"))
//                                           .readEnvironment() // scan environment GIT_* variables
//                                           .findGitDir() // scan up the file system tree
//                                           .build();
//            Git git = new Git(repository);

            git.log()
               .call()
               .forEach(eachLog -> {
                   log.info("Called: {}", eachLog.toString());
               });

        } catch (Exception e) {
            log.error("Error opening repo", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void consumeEvent(Event event) {
        log.info("{} received {}", getId(), event);
    }
}
