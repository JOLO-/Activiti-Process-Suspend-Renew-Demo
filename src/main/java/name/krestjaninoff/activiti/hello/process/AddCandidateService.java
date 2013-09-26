package name.krestjaninoff.activiti.hello.process;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import static name.krestjaninoff.activiti.hello.Main.log;

@Service
public class AddCandidateService implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String executionToken = execution.getProcessDefinitionId() + ":" + execution.getProcessInstanceId() + "\t" + execution.getCurrentActivityName();
        log(executionToken);

        //TODO: next strokes of code are for validation context variables renewal after process suspension
        while (true) {
            log(executionToken + "::" + execution.getVariableLocal("token").toString());
            Thread.currentThread().sleep(1000);
        }
    }
}
