pipeline {
    agent { 
        label 'slave01'
    }
    stages {
        stage('Chekout') {
            steps {
                git branch: 'main', url: "https://github.com/koa5998/LAMP"
            }
        }
        stage('Deploy') {
            steps {
                ansiblePlaybook playbook: 'playbook.yml', inventory: 'hosts.txt', credentialsId: 'private'
            }
        }
        stage('DB existence') {
            steps {
                script {
                    def databaseExists = sh(script: "mysql -h 192.168.88.19 -u postgres -p toortoor -d zapretdb 2>&1 | grep -c 'Unknown database'", returnStatus: true) == 0 ? false : true
                    if (databaseExists) {
                        echo "Database exists"
                        script: "mysql -u postgres -toortoor zapretdb > zapretdb.dump"
                        echo "Made dump"
                    } else {
                        error "Database doesn't exist"
                        script: "mysql -u postgres -toortoor zapretdb < zapretdb.dump"
                    }
                }
            }
        }   
        
    }
}