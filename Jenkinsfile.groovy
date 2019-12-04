properties([parameters([[$class: 'ChoiceParameter', choiceType: 'PT_SINGLE_SELECT', description: 'PLEASE, SELECT YOUR VERSION OF ARTIFACT FOR DEPLOY.', filterLength: 1, filterable: false, name: 'Artifact_Version', randomName: 'choice-parameter-535167446217461', script: [$class: 'GroovyScript', fallbackScript: [classpath: [], sandbox: false, script: 'return[\'error\']'], script: [classpath: [], sandbox: false, script: '''def command = "/usr/local/GraduationWork/select-version/select-artifact-version.sh"
def process = command.execute ( )
process.waitFor() 
def var_arim = [ ]
var_arim = "${process.in.text}" .eachLine { line ->
    var_arim << line
}''']]], [$class: 'ChoiceParameter', choiceType: 'PT_SINGLE_SELECT', description: 'PLEASE, SELECT YOUR VERSION OF IMAGE DOCKER FOR DEPLOY.', filterLength: 1, filterable: false, name: 'Image_Version', randomName: 'choice-parameter-535167449001810', script: [$class: 'GroovyScript', fallbackScript: [classpath: [], sandbox: false, script: 'return[\'error\']'], script: [classpath: [], sandbox: false, script: '''def command = "/usr/local/GraduationWork/select-version/select-image-version.sh"
def process = command.execute ( )
process.waitFor() 
def var_arim = [ ]
var_arim= "${process.in.text}" .eachLine { line ->
    var_arim << line
}''']]]])])

pipeline {
    agent no
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

        stage ("CHOOSE VERSIONS") {
            agent any
            steps {
                echo "${env.Artifact_Version}"
                echo "${env.Image_Version}"
            }
        }


#         stage ("DEPLOY") {
#             agent any        
#             steps {
#                 build 'docker-Instance/deploy_in_docker_repo'
# 
#             parallel (
#                     "ci-Instance" : {
#                         build("ci-Instance/deploy", parameters: [string(name: "Artifact_Version", value: "${env.Artifact_Version}")])
#                         build("ci-Instance/deploy_in_docker_repo", parameters: [string(name: "Image_Version", value: "${env.Image_Version}")])
#                     },
#                     "docker-Instance" : {
#                         build("docker-Instance/deploy", parameters: [string(name: "Artifact_Version", value: "${env.Artifact_Version}")])
#                         build("docker-Instance/deploy_in_docker_repo", parameters: [string(name: "Image_Version", value: "${env.Image_Version}")])
#                     }
#             }, failFast: true
#         }
        
        stage {
            agent no
            // timeout(time: 3, unit: "MINUTES") {
            //    input message: 'Do you want to approve the deploy in production (only for admin user)?', ok: 'Yes'
            //    submitter "admin"
            // } 
            try {
                timeout(time: 30, unit: 'SECONDS') {
                    input message: 'Do you want to approve the deploy in production (only for admin user)?', ok: 'Yes'
                    submitter "admin"
                }
            } catch(err) {
                  def user = err.getCauses()[0].getUser()
                  if (user.toString == 'SYSTEM') {  // if it's system it's a timeout
                      didTimeout = true
                      echo "Build timed out at approval step"
                  } else if (userInput == false) {  // if not and input is false it's the user
                      echo "Build aborted by: [${user}]"
                  }
            }
        }

        stage {
            agent any
            build('qa-Instance/deploy', parameters: [string(name: "Artifact_Version", value: "${env.Artifact_Version}")])
            // build('qa-Instance/deploy_in_docker_repo', parameters: [string(name: "Artifact_Version", value: "${env.Artifact_Version}")])
        }


}
