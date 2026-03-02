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

                        // Obtenemos el nombre del trabajo actual de forma robusta
                        def currentJobName = binding.variables['JOB_NAME']?.split('/')?.last() ?: ""

                        // Filtramos para excluir el proyecto principal de la lista
                        return jenkinsInstance.getAllItems(hudson.model.Job.class).findAll { job ->
                            // Comparamos el nombre completo y el nombre simple para evitar que aparezca
                            job.fullName != currentJobName && job.name != currentJobName
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
        stage('Descargar Código') {
            steps {
                checkout scm
            }
        }

        stage('Ejecución Paralela') {
            steps {
                script {
                    def tareasParalelas = [:]

                    // 1. TAREA LOCAL (El proyecto donde estamos)
                    tareasParalelas["Pruebas Locales"] = {
                        stage("Ejecución Core") {
                            catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                                sh "gradle test --no-daemon --rerun-tasks --info --no-configuration-cache"
                            }
                        }
                    }

                    // 2. TAREAS EXTERNAS (Si se seleccionaron checkboxes)
                    if (params.MODULOS_ADICIONALES) {
                        def seleccionados = params.MODULOS_ADICIONALES.split(',')
                        seleccionados.each { nombre ->
                            def jobName = nombre.trim()
                            if (jobName && !jobName.contains("Esperando") && !jobName.contains("Error")) {
                                tareasParalelas["Job: ${jobName}"] = {
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
            allure includeProperties: false, jdk: '', results: [[path: 'build/allure-results']]
            cleanWs()
        }
    }
}
