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

                        // Obtenemos el nombre del trabajo actual de forma limpia
                        def currentJobName = (binding.hasVariable('JOB_NAME') ? binding.getVariable('JOB_NAME').toString().trim() : "")

                        // Filtramos comparando contra el fullName para exclusión exacta
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
                // Borramos todo rastro físico de la ejecución anterior
                deleteDir()
                sh "rm -rf build/ .gradle/ allure-results/"
                checkout scm
            }
        }

        stage('Ejecución de Pruebas') {
            steps {
                script {
                    def tareasParalelas = [:]

                    // 1. EJECUCIÓN LOCAL (Este proyecto)
                    tareasParalelas["Pruebas Core"] = {
                        catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                            // Clean forzado para regenerar binarios y reportes
                            sh "gradle clean test --no-daemon --rerun-tasks --info --no-configuration-cache"
                        }
                    }

                    // 2. EJECUCIÓN EXTERNA (Otros proyectos)
                    if (params.MODULOS_ADICIONALES) {
                        def seleccionados = params.MODULOS_ADICIONALES.split(',')
                        seleccionados.each { nombre ->
                            def jobName = nombre.trim()
                            if (jobName && !jobName.contains("Esperando") && !jobName.contains("Error")) {
                                tareasParalelas["Extra: ${jobName}"] = {
                                    build job: jobName, wait: true
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
                // Buscamos resultados en la carpeta estándar de Gradle
                allure includeProperties: false, jdk: '', results: [[path: 'build/allure-results']]
            }
            // Limpiamos para evitar problemas en el siguiente build
            cleanWs()
        }
    }
}
