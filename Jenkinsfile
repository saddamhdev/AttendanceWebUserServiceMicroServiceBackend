
pipeline {
    agent any
    tools {
        maven 'maven4'
    }

    environment {
        PROD_USER = "root"                       // FIXED ✔
        PROD_HOST = "159.89.172.251"            // FIXED ✔
        GLOBAL_ENV = '/www/wwwroot/CITSNVN/global.env'
         DEPLOY_DIR = '/www/wwwroot/CITSNVN/attendance/springbootuserservice'
         PORT  = '3080'

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
                    env.JAR_NAME = sh(
                        script: "ls target/*.jar | head -n 1 | xargs -n 1 basename",
                        returnStdout: true
                    ).trim()

                    echo "🟢 Detected JAR: ${env.JAR_NAME}"
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
                        sh '''
                            sshpass -p "$SSH_PASS" ssh -o StrictHostKeyChecking=no ${PROD_USER}@${PROD_HOST} \
                            pkill -f "icsQuizUserService" || echo no-process
                        '''

                        // 2. Fix directory permissions BEFORE starting app
                        sh '''
                            sshpass -p "$SSH_PASS" ssh -o StrictHostKeyChecking=no ${PROD_USER}@${PROD_HOST} \
                            "chmod -R 755 ${DEPLOY_DIR}; \
                            chmod 644 ${DEPLOY_DIR}/*.jar 2>/dev/null || true; \
                            chmod 644 ${DEPLOY_DIR}/*.sh 2>/dev/null || true"
                        '''

                        // 3. Create startup script on VPS with proper environment export
                        sh '''
                            sshpass -p "$SSH_PASS" ssh -T -o StrictHostKeyChecking=no ${PROD_USER}@${PROD_HOST} << SCRIPT
echo "📝 Creating startup script with environment variables..."
cat > ${DEPLOY_DIR}/start.sh << 'EOF'
#!/bin/bash
set -a
source ${GLOBAL_ENV}
set +a
java -jar ${DEPLOY_DIR}/${JAR_NAME} --server.port=${PORT} >> ${DEPLOY_DIR}/app.log 2>&1
EOF
chmod 755 ${DEPLOY_DIR}/start.sh
touch ${DEPLOY_DIR}/app.log 2>/dev/null || true
chmod 644 ${DEPLOY_DIR}/app.log
echo "✅ Startup script created with proper permissions"
ls -lah ${DEPLOY_DIR} | grep -E 'start.sh|app.log|jar'
SCRIPT
                        '''

                        // 4. Start new process using the script
                        sh '''
                            echo "🚀 Starting application..."
                            sshpass -p "$SSH_PASS" ssh -n -o StrictHostKeyChecking=no ${PROD_USER}@${PROD_HOST} \
                            "nohup bash ${DEPLOY_DIR}/start.sh > /dev/null 2>&1 &"
                            echo "⏳ Waiting for application to start..."
                            sleep 2
                        '''

                        // 5. Confirm running
                        sh '''
                            sshpass -p "$SSH_PASS" ssh -o StrictHostKeyChecking=no ${PROD_USER}@${PROD_HOST} \
                            pgrep -f "icsQuizUserService" && echo started || echo failed
                        '''

                        // 6. Display last lines of log
                        sh '''
                            echo "📋 Application Log (Last 30 lines):"
                            echo "=================================="
                            sshpass -p "$SSH_PASS" ssh -o StrictHostKeyChecking=no ${PROD_USER}@${PROD_HOST} \
                            "tail -30 ${DEPLOY_DIR}/app.log || echo 'Log file not available yet'"
                            echo "=================================="
                        '''

                        // 7. Check application health endpoint
                        sh '''
                            echo "🏥 Checking application health status..."
                            sleep 3
                            if curl -s http://${PROD_HOST}:${PORT}/actuator/health | grep -q "UP"; then
                                echo "✅ Application is UP and healthy"
                                curl -s http://${PROD_HOST}:${PORT}/actuator/health | head -20
                            else
                                echo "⚠️  Checking database status from health endpoint..."
                                curl -s http://${PROD_HOST}:${PORT}/actuator/health || echo "Application still starting..."
                            fi
                        '''

                        // 8. Check database status from VPS actuator endpoint
                        sh '''
                            echo ""
                            echo "🗄️  Checking database health from VPS..."
                            sshpass -p "$SSH_PASS" ssh -o StrictHostKeyChecking=no ${PROD_USER}@${PROD_HOST} \
                            "echo 'Database Status:' && \
                            curl -s http://localhost:${PORT}/actuator/health/db 2>/dev/null || echo '⚠️  Database health endpoint not available' && \
                            echo '' && \
                            echo 'Full Health Report:' && \
                            curl -s http://localhost:${PORT}/actuator/health 2>/dev/null | head -50 || echo '⚠️  Health endpoint not responding'"
                        '''

                        // 9. Open firewall port for application
                        sh '''
                            echo ""
                            echo "🔓 Opening firewall port ${PORT}..."
                            sshpass -p "$SSH_PASS" ssh -o StrictHostKeyChecking=no ${PROD_USER}@${PROD_HOST} \
                            "sudo ufw allow ${PORT} && \
                            sudo ufw reload && \
                            echo '✅ Firewall port ${PORT} opened successfully'"
                        '''
                    }
                }
            }

    }

    post {
        success { echo "✅ Deployment Completed Successfully!" }
        failure { echo "❌ Deployment Failed!" }
    }
}

