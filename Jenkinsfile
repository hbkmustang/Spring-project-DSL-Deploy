
node () {

    stage ("PROVISIONING") {
       sh "cd /home/ec2-user/GraduationWork"
       echo ansible playbook different playbooks/roles for provisioning CI instances
       // sh "sh go.sh"
       sh "ansible-playbook -i hosts --limit ci provisioning/site.yml"
    }

    stage ("DEPLOY") {
       echo ansible playbook should deploy latest version of application from Artifactory to CI environment - 8080 clear variant with no root user and 8081 docker variant
       sh "cd /home/ec2-user/GraduationWork"       
       sh "ansible-playbook -i ./hosts --limit -ci deployment/site.yml -e 'port=8080' --vault-password-file deployment/ansible-vault.pass"
    }

}
