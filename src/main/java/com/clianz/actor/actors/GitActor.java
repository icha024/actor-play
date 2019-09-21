package com.clianz.actor.actors;

import com.clianz.actor.actorsystem.BaseActor;
import com.clianz.actor.actorsystem.Event;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.transport.JschConfigSessionFactory;
import org.eclipse.jgit.transport.OpenSshConfig;
import org.eclipse.jgit.transport.SshSessionFactory;
import org.eclipse.jgit.transport.SshTransport;

import java.io.File;

@Slf4j
public class GitActor extends BaseActor {

    @Override
    protected void postConstruct() {
        String branchToClone = "master";
        String gitRepoUri = "ssh://git@github.com/icha024/kube-deploy-sync.git";
        String tmpRepoNamePrefix = "TestGitRepository";
        log.debug("Getting GIT..........");

        try {
            SshSessionFactory sshSessionFactory = createSshSessionFactory();

            File localRepoFilePath = File.createTempFile(tmpRepoNamePrefix, "", new File("/tmp"));
            localRepoFilePath.delete();

            Git git = Git.cloneRepository()
                         .setBranch(branchToClone)
                         .setCloneAllBranches(false)
                         .setTransportConfigCallback(transport -> {
                             SshTransport sshTransport = (SshTransport) transport;
                             sshTransport.setSshSessionFactory(sshSessionFactory);
                         })
                         .setURI(gitRepoUri)
                         .setDirectory(localRepoFilePath)
                         .call();

            git.log()
               .call()
               .forEach(eachLog -> {
                   log.info("Called: {}", eachLog.toString());
               });

        } catch (Exception e) {
            log.error("Error opening repo", e);
//            throw new RuntimeException(e);
        }
    }

    private JschConfigSessionFactory createSshSessionFactory() {
        return new JschConfigSessionFactory() {
            @Override
            protected void configure(OpenSshConfig.Host hc, Session session) {
                // Do nothing
            }

            @Override
            protected void configureJSch(JSch jsch) {
                super.configureJSch(jsch);
                try {
                    jsch.addIdentity("/tmp/kube-dep-sync_id_rsa");
                    log.debug("CONFIGURED PRIVATE KEY.....");
                } catch (JSchException e) {
                    log.error("Error connecting to Git", e);
//                    throw new RuntimeException(e);
                }
            }
        };
    }

    @Override
    protected void consumeEvent(Event event) {
        log.info("{} received {}", getId(), event);
    }
}
