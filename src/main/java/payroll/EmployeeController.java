package payroll;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
class EmployeeController {
	// una vez iniciado el repositorio se mantiene constante
	private final EmployeeRepository repository;
	
	//constuctor de la clase
	EmployeeController(EmployeeRepository repository){
		this.repository = repository;
	}
	// endpoint (ruta) que recibe la petición Get para listar los empleados
	@GetMapping("/employees")
	List<Employee> all(){
		return repository.findAll();
	}
	// crea un recurso, el método pasa los parametros del nuevo empleado con @Requesbody y lo graba
	@PostMapping("/employees")
	Employee newEmployee(@RequestBody Employee newEmployee) {
		return repository.save(newEmployee);
	}
	
	
	// busca un item si no existe lo crea - lambda implementa interfaz funcional
	@GetMapping("/employees/{id}")
	Employee one(@PathVariable Long id) {
		return repository.findById(id).orElseThrow(()-> new EmployeeNotFoundException(id));
	}
	
	@PutMapping("employees/{id}")	
	Employee replaceEmployee(@RequestBody Employee newEmployee, @PathVariable Long id) {
		return repository.findById(id).map(employee->{
				employee.setName(newEmployee.getName());
				employee.setRole(newEmployee.getRole());
				return repository.save(employee);
		})
		.orElseGet(()->{
			return repository.save(newEmployee);
		});
	}
	
	@DeleteMapping("/employees/{id}")
	void deleteEmployee(@PathVariable Long id) {
		repository.deleteById(id);
	}

}
