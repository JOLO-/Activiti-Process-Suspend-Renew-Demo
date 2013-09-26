package name.krestjaninoff.activiti.hello.process;


import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;


@Service
public class SaveVacancyAndAssignService implements JavaDelegate {

	public void execute(DelegateExecution execution) throws Exception {
        System.out.println(execution.getProcessDefinitionId() + ":" + execution.getProcessInstanceId() + "\t" + execution.getCurrentActivityName());
    }
}
