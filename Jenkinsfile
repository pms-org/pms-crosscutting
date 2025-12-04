pipeline {
    agent any
    
    tools {
        maven 'Maven-3.9'
        jdk 'JDK-21'
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('Test') {
            steps {
                sh 'mvn clean test'
            }
            post {
                always {
                    publishTestResults testResultsPattern: 'target/surefire-reports/*.xml'
                }
            }
        }
        
        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }
        
        stage('Docker Build') {
            steps {
                sh 'docker build -t pms-crossref:${BUILD_NUMBER} .'
            }
        }
        
        stage('Deploy') {
            when {
                branch 'main'
            }
            steps {
                sh 'docker tag pms-crossref:${BUILD_NUMBER} pms-crossref:latest'
                // Add deployment steps here
            }
        }
    }
}