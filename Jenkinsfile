pipeline {
    agent any

    environment {
        PROD_HOST  = credentials('DO_HOST')
        PROD_USER  = credentials('DO_USER')
        DEPLOY_DIR = '/www/wwwroot/CITSNVN/jenkins/sscSpringbootBackend'
        JAR_NAME   = 'quizbackend-1.0.jar'
        PORT       = '3086'
    }

    stages {
        stage('Clone Repository') {
            steps {
                git branch: 'main', url: 'https://github.com/Saddam-Hossen/JenkinsSpringbootProjectBackend'
            }
        }

        stage('Build') {
            steps {
                bat 'mvn clean install'
            }
        }

        stage('Deploy JAR to Server') {
            steps {
                withCredentials([sshUserPrivateKey(credentialsId: 'DO_SSH_KEY', keyFileVariable: 'SSH_KEY')]) {
                    script {
                        bat """
                        "C:/Program Files/Git/bin/bash.exe" -c "scp -o StrictHostKeyChecking=no -i '${SSH_KEY}' target/${JAR_NAME} ${PROD_USER}@${PROD_HOST}:${DEPLOY_DIR}/${JAR_NAME}"
                        """
                    }
                }
            }
        }

        stage('Start Spring Boot App (Remote)') {
            steps {
                withCredentials([sshUserPrivateKey(credentialsId: 'DO_SSH_KEY', keyFileVariable: 'SSH_KEY')]) {
                    script {
                        bat """
                        "C:/Program Files/Git/bin/bash.exe" -c "ssh -o StrictHostKeyChecking=no -i '${SSH_KEY}' ${PROD_USER}@${PROD_HOST} 'cd ${DEPLOY_DIR}; PID=\$(lsof -t -i:${PORT}); if [ ! -z \$PID ]; then kill -9 \$PID; fi; nohup java -Xms32m -Xmx64m -jar ${JAR_NAME} --server.port=${PORT} > app.log 2>&1 &'"
                        """
                    }
                }
            }
        }

    }

    post {
        failure {
            echo "❌ Spring Boot deployment failed."
        }
        success {
            echo "✅ Spring Boot deployed successfully."
        }
    }
}