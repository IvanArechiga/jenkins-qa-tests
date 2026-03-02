// PROPIEDADES: Configuración de la interfaz de usuario con Active Choices
properties([
    parameters([
        [$class: 'ChoiceParameter',
            choiceType: 'PT_CHECKBOX',
            description: 'Selecciona módulos ADICIONALES a ejecutar en paralelo:',
            name: 'MODULOS_ADICIONALES',
            script: [
                $class: 'GroovyScript',
                fallbackScript: [sandbox: false, script: 'return ["Error: Ejecuta el build una vez y aprueba el script"]'],
                script: [sandbox: false, script: '''
                    return jenkins.model.Jenkins.get().getAllItems(hudson.model.Job.class).findAll {
                        it.fullName != "QA-Automatizacion-Core"
                    }.collect { it.fullName }
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

                    // 1. PROYECTO ACTUAL (Core)
                    tareasParalelas["Proyecto Principal"] = {
                        stage("Principal") {
                            catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                                // Usamos --no-daemon para garantizar el cierre del proceso
                                sh "gradle test --no-daemon --rerun-tasks --info --no-configuration-cache"
                            }
                        }
                    }

                    // 2. PROYECTOS ADICIONALES
                    if (params.MODULOS_ADICIONALES) {
                        def seleccionados = params.MODULOS_ADICIONALES.split(',')
                        seleccionados.each { nombre ->
                            if (nombre && !nombre.contains("Error")) {
                                tareasParalelas["Extra: ${nombre}"] = {
                                    // El 'build job' disparará el otro proyecto en su propio hilo
                                    build job: "${nombre.trim()}", wait: true
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
