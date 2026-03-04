properties([
    parameters([
        [$class: 'ChoiceParameter',
            choiceType: 'PT_CHECKBOX',
            description: 'Selecciona módulos ADICIONALES a ejecutar en paralelo:',
            name: 'MODULOS_ADICIONALES',
            script: [
                $class: 'GroovyScript',
                fallbackScript: [sandbox: false, script: 'return ["Esperando aprobación de seguridad..."]'],
                script: [sandbox: false, script: '''
                    try {
                        def jenkinsInstance = jenkins.model.Jenkins.get()

                        def currentJobName = binding.hasVariable('jenkinsProject') ? jenkinsProject.fullName : ""

                        return jenkinsInstance.getAllItems(hudson.model.Job.class).findAll { job ->
                            job.fullName.toString().trim() != currentJobName
                        }.collect { it.fullName }.sort()
                    } catch (Exception e) {
                        return ["Error: " + e.getMessage()]
                    }
                ''']
            ]
        ]
    ])
])

pipeline {
    agent any

    tools {
        jdk 'JDK21'
        gradle 'Gradle'
    }

    stages {
        stage('Limpieza Profunda') {
            steps {
                deleteDir()
                sh "rm -rf build/ .gradle/ allure-results/"
                checkout scm
            }
        }

        stage('Ejecución de Pruebas') {
            steps {
                script {
                    def tareasParalelas = [:]

                    tareasParalelas["Pruebas Core"] = {
                        catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                            sh "gradle clean test --no-daemon --rerun-tasks --info --no-configuration-cache"
                        }
                    }

                    if (params.MODULOS_ADICIONALES) {
                        def seleccionados = params.MODULOS_ADICIONALES.split(',')
                        seleccionados.each { nombre ->
                            def jobName = nombre.trim()

                            if (jobName && jobName != env.JOB_NAME && !jobName.contains("Esperando") && !jobName.contains("Error")) {
                                tareasParalelas["Extra: ${jobName}"] = {

                                    build job: jobName, wait: true, parameters: [
                                        string(name: 'MODULOS_ADICIONALES', value: '')
                                    ]
                                }
                            }
                        }
                    }

                    parallel tareasParalelas
                }
            }
        }
    }

    post {
        always {
            script {
                echo 'Generando reporte final Allure...'
                allure includeProperties: false, jdk: '', results: [[path: 'build/allure-results']]
            }
            cleanWs()
        }
    }
}