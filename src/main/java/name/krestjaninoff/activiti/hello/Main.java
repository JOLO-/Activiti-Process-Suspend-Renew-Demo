package name.krestjaninoff.activiti.hello;


import org.activiti.engine.*;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.List;

public class Main {

    private final static int VACANCY_PROCESS_AMOUNT = 2;
    private final static int CANDIDATE_PROCESS_AMOUNT = 1;
    private final static int DELAY_BEFORE_SUSPEND = 5000;
    private final static List<ProcessInstance> processInstances = new ArrayList<ProcessInstance>();

    /**
     * Activiti services
     */
    private static ClassPathXmlApplicationContext applicationContext;
    private static RuntimeService runtimeService;
    private static TaskService taskService;

    static {
        applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
        runtimeService = (RuntimeService) applicationContext.getBean("runtimeService");
        taskService = (TaskService) applicationContext.getBean("taskService");
    }

	public static void main(String[] args) throws  Exception {
        if (!areSuspendedProcessInstances()) {
            log("launch VACANCY_PROCESS_AMOUNT processes");
            launchVancyProcesses();
            ckeckMainProcessLaunch();

            log("start two subprocesses for each vacancy subprocess");
            for (ProcessInstance processInstance : runtimeService.createProcessInstanceQuery().active().list())
                startCandidateSubprocess(processInstance.getId(), CANDIDATE_PROCESS_AMOUNT);
        } else {
            log("renew suspended processes");
            activateSuspendedProcesses();
            for(ProcessInstance processInstance : runtimeService.createProcessInstanceQuery().active().list())
                checkSubprocessLaunch(processInstance.getProcessInstanceId());
        }

        Thread.currentThread().sleep(DELAY_BEFORE_SUSPEND);
        log("suspend all active processes");
        suspendActiveProcesses();
    }

    private  static boolean areSuspendedProcessInstances() {
        log("areSuspendedProcessInstances");
        return !runtimeService.createProcessInstanceQuery().suspended().list().isEmpty();
    }

    private static void activateSuspendedProcesses() {
        log("activateSuspendedProcesses");
        List<ProcessInstance> suspendedProcesses = runtimeService.createProcessInstanceQuery().suspended().list();
        for (ProcessInstance processInstance : suspendedProcesses) {
            log("Activate process instance with id " + processInstance.getProcessInstanceId());
            runtimeService.activateProcessInstanceById(processInstance.getProcessInstanceId());
        }
    }

    private static void suspendActiveProcesses() {
        log("suspendActiveProcesses");
        for (ProcessInstance processInstance : processInstances) {
            log("Suspend process with id " + processInstance.getProcessInstanceId());
            runtimeService.suspendProcessInstanceById(processInstance.getProcessInstanceId());
        }
    }

    /**
     * Launch VACANCY_PROCESS_AMOUNT vacancy subprocesses, and save their instances.
     */
    private static void launchVancyProcesses() {
        log("launchVancyProcesses");
        for (int i = 0; i < VACANCY_PROCESS_AMOUNT; i++) {
            log("launchVancyProcess --- ");
            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("hiringProcess");
            log("\t\t\t\t\t---- " + processInstance.getProcessInstanceId());
            processInstances.add(processInstance);
        }
    }

    /**
     * Check if vacancy project has been launched in the right way.
     * @throws IllegalStateException
     */
    private static void ckeckMainProcessLaunch() throws IllegalStateException {
        log("ckeckMainProcessLaunch");
        for (ProcessInstance processInstance: processInstances) {
            List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstance.getProcessInstanceId()).list();

            if (tasks.size() != 1)
                throw new IllegalStateException();
            Task currentTask = tasks.get(0);
            if (!currentTask.getName().equals("Find employees"))
                throw new IllegalStateException();
        }
    }

    /**
     * Start the subprocessAmount subprocesses for vacancy process with id vacancyProcessId
     * @param vacancyProcessId id of the process from which we will start subprocesses
     * @param subprocessAmount amount of subprocesses
     */
    private static void startCandidateSubprocess(String vacancyProcessId, int subprocessAmount) {
        log("startCandidateSubprocess:: vacancyProcessId = " + vacancyProcessId + " subprocessAmount = " + subprocessAmount);
        //find the needed execution, to which we address the signal
        List<Execution> executions = runtimeService.createExecutionQuery().signalEventSubscriptionName("launchCandidateSubprocess").list();
        Execution execution = null;
        for (int i = 0; i < executions.size(); i++)
            if (executions.get(i).getProcessInstanceId().equals(vacancyProcessId))
                execution = executions.get(i);

        //launch the CANDIDATE_PROCESS_AMOUNT candidates subprocesses fir first
        for (int i = 0; i < subprocessAmount; i++) {
            runtimeService.signalEventReceived("launchCandidateSubprocess", execution.getId());
        }
    }

    private static void checkSubprocessLaunch(String vacancyProcessId) {
        log("checkSubprocessLaunch:: vacancyProcessId" + vacancyProcessId);
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(vacancyProcessId).list();
        if (tasks.size() != CANDIDATE_PROCESS_AMOUNT + 1)
            throw new IllegalStateException("there are " + tasks.size() + " tasks, but should be " + (CANDIDATE_PROCESS_AMOUNT + 1));
    }

    public synchronized static void log(String msg) {
        System.out.println(msg);
    }
}
