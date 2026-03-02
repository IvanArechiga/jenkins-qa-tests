properties([
    parameters([
        [$class: 'ChoiceParameter',
            choiceType: 'PT_CHECKBOX',
            description: 'Selecciona módulos ADICIONALES a ejecutar en paralelo:',
            name: 'MODULOS_ADICIONALES',
            script: [
                $class: 'GroovyScript',
                fallbackScript: [sandbox: false, script: 'return ["Esperando aprobación de script en Jenkins..."]'],
                script: [sandbox: false, script: '''
                    try {
                        // Acceso al motor de Jenkins
                        def jenkinsInstance = jenkins.model.Jenkins.get()

                        // FIX: Obtenemos el JOB_NAME desde el binding del script, no desde env
                        def currentJobName = binding.variables.get('JOB_NAME') ?: ""

                        // Obtenemos todos los Jobs excluyendo el actual para evitar recursividad
                        return jenkinsInstance.getAllItems(hudson.model.Job.class).findAll { job ->
                            job.fullName != currentJobName
                        }.collect { it.fullName }.sort()
                    } catch (Exception e) {
                        return ["Error de permisos: " + e.getMessage()]
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

        stage('Ejecución de Pruebas en Paralelo') {
            steps {
                script {
                    def tareasParalelas = [:]

                    // 1. PROYECTO ACTUAL (Core/Principal)
                    tareasParalelas["Pruebas Locales"] = {
                        catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                            sh "gradle test --no-daemon --rerun-tasks --info --no-configuration-cache"
                        }
                    }

                    // 2. PROYECTOS ADICIONALES (Orquestación Downstream)
                    if (params.MODULOS_ADICIONALES) {
                        def seleccionados = params.MODULOS_ADICIONALES.split(',')
                        seleccionados.each { nombre ->
                            def jobName = nombre.trim()
                            if (jobName && !jobName.contains("Esperando") && !jobName.contains("Error")) {
                                tareasParalelas["Job: ${jobName}"] = {
                                    stage("Disparando: ${jobName}") {
                                        build job: jobName, wait: true
                                    }
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
            echo 'Consolidando resultados Allure...'
            allure includeProperties: false, jdk: '', results: [[path: 'build/allure-results']]
            cleanWs()
        }
    }
}
