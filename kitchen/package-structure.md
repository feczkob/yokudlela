```
project
    Application.java
    application
        configuration (ModelMapper, ObjectMapper)
        error (ControllerAdvice, TechnicalException)
    controller
        topicA
            TopicAController.java
            request
            response
            validation
            converter
        topicB ...
    business
        topicA
            TopicAManager.java
            model (TopicABusinessModel1.java, TopicABusinessModel2.java, TopicAHandler.java, ...)
            service (TopicAService.java)
        topicB ...
        error (BusinessException.java, ErrorType.java)
    client
        persistence
            repo, entity
        rest
            config -> ApiClient.java
        amqp
```
