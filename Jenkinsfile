
pipeline {
    agent any
    tools {
        maven 'maven4'
    }

    environment {
        PROD_USER = "root"                       // FIXED ✔
        PROD_HOST = "159.89.172.251"            // FIXED ✔
        DEPLOY_DIR = '/www/wwwroot/CITSNVN/attendance/springbootuserservice'
        PORT       = '3080'
    }

    stages {

        stage('Verify Credentials') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'DO_SSH_PASSWORD',
                                                 usernameVariable: 'SSH_USER',
                                                 passwordVariable: 'SSH_PASS')]) {
                    echo "🟢 Credentials are OK."
                }
            }
        }

        stage('Debug Vars') {
            steps {
                sh '''
                    echo HOST=$PROD_HOST
                    echo USER=$PROD_USER
                '''
            }
        }

        stage('Clone Repository') {
            steps {
                git branch: 'main', url: 'https://github.com/Saddam-Hossen/AttendanceWebUserServiceMicroServiceBackend'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests'
                sh 'echo ==== BUILT FILES ===='
                sh 'ls -lah target'
            }
        }

        stage('Detect Built JAR') {
            steps {
                script {
                    JAR_NAME = sh(
                        script: "ls target/*.jar | head -n 1 | xargs -n 1 basename",
                        returnStdout: true
                    ).trim()

                    echo "🟢 Detected JAR: ${JAR_NAME}"
                }
            }
        }

       stage('Upload JAR to VPS') {
           steps {
               withCredentials([usernamePassword(credentialsId: 'DO_SSH_PASSWORD',
                                                usernameVariable: 'SSH_USER',
                                                passwordVariable: 'SSH_PASS')]) {

                   sh """
                       echo "📤 Uploading JAR to server..."

                       sshpass -p "$SSH_PASS" scp -o StrictHostKeyChecking=no \
                           target/${JAR_NAME} ${PROD_USER}@${PROD_HOST}:${DEPLOY_DIR}/${JAR_NAME}
                   """
               }
           }
       }

            stage('Restart App on VPS') {
                steps {
                    withCredentials([
                        usernamePassword(
                            credentialsId: 'DO_SSH_PASSWORD',
                            usernameVariable: 'SSH_USER',
                            passwordVariable: 'SSH_PASS'
                        )
                    ]) {

                        sh 'echo "Restarting app on VPS..."'

                        // 1. Kill old process
                        sh """
                            sshpass -p "$SSH_PASS" ssh -o StrictHostKeyChecking=no ${PROD_USER}@${PROD_HOST} \
                            pkill -f ${JAR_NAME} || echo no-process
                        """

                        // 2. Fix directory permissions BEFORE starting app
                        sh """
                            sshpass -p "$SSH_PASS" ssh -o StrictHostKeyChecking=no ${PROD_USER}@${PROD_HOST} \
                            chmod -R 777 ${DEPLOY_DIR}
                        """

                        // 3. Start new process
                        sh """
                            sshpass -p "$SSH_PASS" ssh -o StrictHostKeyChecking=no ${PROD_USER}@${PROD_HOST} \
                            "nohup java -jar ${DEPLOY_DIR}/${JAR_NAME} --server.port=${PORT} >> ${DEPLOY_DIR}/app.log 2>&1 &"
                        """

                        // 4. Confirm running
                        sh """
                            sshpass -p "$SSH_PASS" ssh -o StrictHostKeyChecking=no ${PROD_USER}@${PROD_HOST} \
                            pgrep -f ${JAR_NAME} && echo started || echo failed
                        """
                    }
                }
            }




    }

    post {
        success { echo "✅ Deployment Completed Successfully!" }
        failure { echo "❌ Deployment Failed!" }
    }
}
