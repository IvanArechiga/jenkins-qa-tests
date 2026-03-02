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
                        // Obtenemos el nombre exacto del JOB_NAME del binding
                        def currentJobName = binding.variables['JOB_NAME']?.toString() ?: ""

                        return jenkinsInstance.getAllItems(hudson.model.Job.class).findAll { job ->
                            // Filtrado estricto por fullName
                            job.fullName.toString() != currentJobName
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
                // Borramos todo rastro físico de la ejecución anterior en el workspace
                deleteDir()
                sh "rm -rf allure-results build .gradle"
                checkout scm
            }
        }

        stage('Ejecución de Pruebas') {
            steps {
                script {
                    def tareasParalelas = [:]

                    // 1. EJECUCIÓN LOCAL (Core)
                    tareasParalelas["Pruebas Core"] = {
                        catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                            // Importante: forzamos que Gradle use la carpeta allure-results en la raíz
                            sh "gradle clean test -Dallure.results.directory=allure-results --no-daemon --rerun-tasks --info"
                        }
                    }

                    // 2. EJECUCIÓN EXTERNA (Orquestación)
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
                echo 'Generando reporte consolidado de Core...'
                // Buscamos resultados en la carpeta que forzamos en el comando sh
                allure includeProperties: false, jdk: '', results: [[path: 'allure-results']]
            }
            // Mantenemos el workspace limpio para el siguiente build
            cleanWs()
        }
    }
}