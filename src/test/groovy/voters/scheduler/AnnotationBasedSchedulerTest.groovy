package voters.scheduler

import org.springframework.beans.factory.config.AutowireCapableBeanFactory
import org.springframework.context.ApplicationContext
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor
import spock.lang.Specification

class AnnotationBasedSchedulerTest extends Specification {

    AnnotationBasedScheduler scheduler

    ApplicationContext applicationContext

    def "enable scheduler"() {
        given:
        def schedulerProcessor = Mock(ScheduledAnnotationBeanPostProcessor)
        def beanFactory = Mock(AutowireCapableBeanFactory)
        1 * beanFactory.createBean(ScheduledAnnotationBeanPostProcessor) >> schedulerProcessor
        1 * applicationContext.autowireCapableBeanFactory >> beanFactory
        1 * applicationContext.getBeanDefinitionNames() >> ['bean']
        def someBean = new Object()
        1 * applicationContext.getBean('bean') >> someBean

        when:
        scheduler.enable()

        then:
        1 * schedulerProcessor.postProcessAfterInitialization(someBean, 'bean') >> someBean
        1 * schedulerProcessor.onApplicationEvent(_) >> { ContextRefreshedEvent e ->
            assert e.applicationContext == applicationContext
        }
    }

    def "disable scheduler"() {
        given:
        scheduler.scheduledAwareScheduler = Mock(ScheduledAnnotationBeanPostProcessor)
        def beanFactory = Mock(AutowireCapableBeanFactory)
        1 * applicationContext.autowireCapableBeanFactory >> beanFactory

        when:
        scheduler.disable()

        then:
        1 * beanFactory.destroyBean(scheduler.scheduledAwareScheduler)
        !scheduler.scheduledAwareScheduler
    }

    def "get name"() {
        when:
        def name = scheduler.getName()

        then:
        name == 'node'
    }

    def "return true if disabled"() {
        when:
        def status = scheduler.isDisabled()

        then:
        status
    }

    def setup() {
        applicationContext = Mock(ApplicationContext)
        scheduler = new AnnotationBasedScheduler('node')
        scheduler.applicationContext = applicationContext
    }
}
