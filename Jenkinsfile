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
                    // Obtenemos el nombre del proyecto actual dinámicamente
                    def currentJob = jenkins.model.Jenkins.get().getItem(JOB_NAME)?.fullName

                    return jenkins.model.Jenkins.get().getAllItems(hudson.model.Job.class).findAll {
                        // Filtro dinámico: No mostrar el proyecto donde estamos parados
                        it.fullName != currentJob
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

                    tareasParalelas["Pruebas Locales"] = {
                        catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                            sh "gradle test --no-daemon --rerun-tasks --info --no-configuration-cache"
                        }
                    }

                    if (params.MODULOS_ADICIONALES) {
                        def seleccionados = params.MODULOS_ADICIONALES.split(',')
                        seleccionados.each { nombre ->
                            if (nombre && !nombre.contains("Error")) {
                                tareasParalelas["Job: ${nombre}"] = {
                                    // 'wait: true' es necesario para consolidar el reporte Allure al final
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
            echo 'Consolidando resultados Allure...'
            allure includeProperties: false, jdk: '', results: [[path: 'build/allure-results']]
            cleanWs()
        }
    }
}