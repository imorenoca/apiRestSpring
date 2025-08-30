package payroll;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(EmployeeController.class)
public class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmployeeRepository repository;

    private Employee employee1;
    private Employee employee2;

    @BeforeEach
    void setup() {
        employee1 = new Employee("Frodo Baggins", "ring bearer");
        employee1.setId(1L);
        employee2 = new Employee("Bilbo Baggins", "burglar");
        employee2.setId(2L);
    }

    @Test
    void shouldReturnAllEmployees() throws Exception {
        when(repository.findAll()).thenReturn(Arrays.asList(employee1, employee2));

        mockMvc.perform(get("/employees"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].name").value("Frodo Baggins"))
               .andExpect(jsonPath("$[1].role").value("burglar"));
    }

    @Test
    void shouldReturnEmployeeById() throws Exception {
        when(repository.findById(1L)).thenReturn(Optional.of(employee1));

        mockMvc.perform(get("/employees/1"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.name").value("Frodo Baggins"))
               .andExpect(jsonPath("$.role").value("ring bearer"));
    }

    @Test
    void shouldCreateNewEmployee() throws Exception {
        Employee newEmployee = new Employee("Samwise Gamgee", "gardener");
        when(repository.save(any(Employee.class))).thenReturn(newEmployee);

        mockMvc.perform(post("/employees")
               .contentType(MediaType.APPLICATION_JSON)
               .content("{\"name\":\"Samwise Gamgee\", \"role\":\"gardener\"}"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.name").value("Samwise Gamgee"));
    }

    @Test
    void shouldUpdateEmployee() throws Exception {
        Employee updatedEmployee = new Employee("Frodo Baggins", "Fellowship member");
        when(repository.findById(1L)).thenReturn(Optional.of(employee1));
        when(repository.save(any(Employee.class))).thenReturn(updatedEmployee);

        mockMvc.perform(put("/employees/1")
               .contentType(MediaType.APPLICATION_JSON)
               .content("{\"name\":\"Frodo Baggins\", \"role\":\"Fellowship member\"}"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.role").value("Fellowship member"));
    }

    @Test
    void shouldDeleteEmployee() throws Exception {
        mockMvc.perform(delete("/employees/1"))
               .andExpect(status().isOk());
        
        verify(repository, times(1)).deleteById(1L);
    }
}
