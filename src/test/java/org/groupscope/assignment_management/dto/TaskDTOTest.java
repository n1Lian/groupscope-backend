package org.groupscope.assignment_management.dto;

import io.jsonwebtoken.lang.Assert;
import org.groupscope.assignment_management.entity.Task;
import org.groupscope.assignment_management.entity.TaskType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.Objects;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class TaskDTOTest {

    @Test
    public void taskDTOTest(){
        final String name = "Name";
        final TaskType taskType = TaskType.PRACTICAL;
        final String info = "Info";
        final String deadLine = "04.12.2023";
        final Integer maxMark = 100;
        final TaskDTO taskDTO = new TaskDTO(name, taskType, info, deadLine, maxMark);
        assertEquals(name, taskDTO.getName());
        assertEquals(taskType, taskDTO.getType());
        assertEquals(info, taskDTO.getInfo());
        assertEquals(deadLine, taskDTO.getDeadline());
        assertEquals(maxMark, taskDTO.getMaxMark());
    }

    //todo
    /*@Test
    public void fromTest(){

    }*/

    @Test
    public void toTaskTest(){
        TaskDTO taskDTO = new TaskDTO("Name", TaskType.PRACTICAL, "Info",
                "04.12.2023", 100);
        Task task = taskDTO.toTask();
        assertEquals(taskDTO.getId(), task.getId());
        assertEquals(taskDTO.getName(), task.getName());
        assertEquals(taskDTO.getType(), task.getType());
        assertEquals(taskDTO.getInfo(), task.getInfo());
        assertEquals(taskDTO.getDeadline(), task.getDeadline());
        assertEquals(taskDTO.getMaxMark(), task.getMaxMark());
    }

    @Test
    public void toTaskExceptionTest(){
        TaskDTO taskDTO1 = new TaskDTO("Name", TaskType.PRACTICAL, "Info",
                "04.13.2023", 100);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            Task task = taskDTO1.toTask();
        });

        String expectedMessage = "Wrong task type or date format in Task object";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

        TaskDTO taskDTO2 = new TaskDTO("Name", TaskType.PRACTICAL, "Info",
                "04.12.2023", 0);

        exception = assertThrows(IllegalArgumentException.class, () -> {
            Task task = taskDTO2.toTask();
        });

        expectedMessage = "Wrong task type or date format in Task object";
        actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void isValidTest(){
        TaskDTO taskDTO1 = new TaskDTO("Name", TaskType.PRACTICAL, "Info",
                "DeadLine", 100);
        assertFalse(taskDTO1.isValid());

        TaskDTO taskDTO2 = new TaskDTO("Name", TaskType.PRACTICAL, "Info",
                "05.12.2023", 0);
        assertFalse(taskDTO2.isValid());

        TaskDTO taskDTO3 = new TaskDTO("Name", TaskType.PRACTICAL, "Info",
                "05.12.2023", 100);
        assertTrue(taskDTO3.isValid());
    }

    @Test
    public void isValidDeadLineTest(){
        TaskDTO taskDTO1 = new TaskDTO("Name", TaskType.PRACTICAL, "Info",
                "DeadLine", 100);
        assertFalse(taskDTO1.isValidDeadline());

        TaskDTO taskDTO2 = new TaskDTO("Name", TaskType.PRACTICAL, "Info",
                "05.15.2023", 100);
        assertFalse(taskDTO2.isValidDeadline());

        TaskDTO taskDTO3 = new TaskDTO("Name", TaskType.PRACTICAL, "Info",
                "40.12.2023", 100);
        assertFalse(taskDTO3.isValidDeadline());

        TaskDTO taskDTO4 = new TaskDTO("Name", TaskType.PRACTICAL, "Info",
                "05.12.2023", 100);
        assertTrue(taskDTO4.isValidDeadline());
    }

    @Test
    public void isValidMaxMarkTest(){
        TaskDTO taskDTO1 = new TaskDTO("Name", TaskType.PRACTICAL, "Info",
                "04.12.2023", 0);
        assertFalse(taskDTO1.isValidMaxMark());

        TaskDTO taskDTO2 = new TaskDTO("Name", TaskType.PRACTICAL, "Info",
                "04.12.2023", 20);
        assertTrue(taskDTO2.isValidMaxMark());
    }

    @Test
    public void hashCodeTheSameValueTest() {
        final String name = "Name";
        final TaskType taskType = TaskType.PRACTICAL;
        final String info = "Info";
        final String deadLine = "04.12.2023";
        final Integer maxMark = 100;
        final TaskDTO taskDTO = new TaskDTO(name, taskType, info, deadLine, maxMark);
        int testHash = Objects.hash(name, taskType, info, deadLine, maxMark);
        assertEquals(testHash, taskDTO.hashCode());
    }

}
