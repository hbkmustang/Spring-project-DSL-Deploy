properties([parameters([[$class: 'ChoiceParameter', choiceType: 'PT_SINGLE_SELECT', description: 'PLEASE, SELECT YOUR VERSION OF ARTIFACT FOR DEPLOY.', filterLength: 1, filterable: false, name: 'Artifact_Version', randomName: 'choice-parameter-535167446217461', script: [$class: 'GroovyScript', fallbackScript: [classpath: [], sandbox: false, script: 'return[\'error\']'], script: [classpath: [], sandbox: false, script: '''def command = "/usr/local/GraduationWork/select-version/select-artifact-version.sh"
                         def process = command.execute ( )
                         process.waitFor() 
                         def var_arim = [ ]
                         var_arim = "${process.in.text}" .eachLine { line ->
                             var_arim << line
}''']]],[$class: 'ChoiceParameter', choiceType: 'PT_SINGLE_SELECT', description: 'PLEASE, SELECT YOUR VERSION OF IMAGE DOCKER FOR DEPLOY.', filterLength: 1, filterable: false, name: 'ImageVersion', randomName: 'choice-parameter-535167449001810', script: [$class: 'GroovyScript', fallbackScript: [classpath: [], sandbox: false, script: 'return[\'error\']'], script: [classpath: [], sandbox: false, script: '''def command = "/usr/local/GraduationWork/select-version/select-image-version.sh"
         def process = command.execute ( )
         process.waitFor() 
         def var_arim = [ ]
         var_arim= "${process.in.text}" .eachLine { line ->
             var_arim << line
}''']]]])])

pipeline {
    agent none
//    parameters {
//        choice(
//            name: 'Versions',
//            choices:"3.4\n4.4",
//            description: "Build for which version?" )
//        activechoice(
//            name: 'Versions_2',
//            choices:"3.4\n4.4",
//            description: "Build for which version 2?" )
//    }        

    
    stages {


//        stage ("Find all artifact and image versions") {
//            steps {
//                fileContent = sh (
//                    script: '/usr/local/GraduationWork/select-version/select-artifact-version.sh',
//                    returnStdout: true
//                ).trim()
//            }
//            steps {
//                fileContent2 = sh (
//                    script: '/usr/local/GraduationWork/select-version/select-image-version.sh',
//                    returnStdout: true
//                ).trim()
//           }
//        }
//    }

         stage ("OUTPUT CHOICED VERSIONS") {
             agent any
             steps {
                 echo "${env.ArtifactVersion}"
                 echo "${env.ImageVersion}"
             }
         }


//         stage ("DEPLOY") {
//             agent any        
//             steps {
//                 build 'docker-Instance/deploy_in_docker_repo'
// 
//             parallel (
//                     "ci-Instance" : {
//                         build("ci-Instance/deploy", parameters: [string(name: "ArtifactVersion", value: "${env.ArtifactVersion}")])
//                         build("ci-Instance/deploy_in_docker_repo", parameters: [string(name: "ImageVersion", value: "${env.Image_Version}")])
//                     },
//                     "docker-Instance" : {
//                         build("docker-Instance/deploy", parameters: [string(name: "ArtifactVersion", value: "${env.ArtifactVersion}")])
//                         build("docker-Instance/deploy_in_docker_repo", parameters: [string(name: "ImageVersion", value: "${env.ImageVersion}")])
//                     }
//             }, failFast: true
//         }
        
         stage ("APPROVAL FOR DEPLOY TO QA") {
             agent none
             steps {
                 timeout(time: 30, unit: 'SECONDS') {
                         input id: "Deploy", message: "Do you want to approve deploy to QA (only for admin user)?", submitter: "admin"
                 }
             }
         }
 
         stage ("DEPLOY TO QA") {
             agent any
             steps {
                 build job: 'action-Instance/deploy', wait: true, parameters: [string(name: "ArtifactVersion", value: "${env.ArtifactVersion}"), string(name: "InstanceName", value: "docker")]
                 build job: 'action-Instance/deploy_in_docker_repo', wait: true,  parameters: [string(name: "ImageVersion", value: "${env.ImageVersion}"), string(name: "InstanceName", value: "docker")]
             }
         }


    }
}
