package com.grokonez.jwtauthentication.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.grokonez.jwtauthentication.model.Employee;
import com.grokonez.jwtauthentication.service.EmployeeService;

@RestController
public class EmployeeController {

	@Autowired
    private EmployeeService employeeService;
	
	// get all employees
	@GetMapping("/employees")
	public List<Employee> getAllEmployees(){
		return employeeService.getEmployee();
	}		
	
	// create employee rest api
	@PostMapping("/employees")
	public Employee createEmployee(@RequestBody Employee employee) {
		return employeeService.addEmployee(employee);
	}
	
	// get employee by id rest api
	@GetMapping("/employees/{user_id}")
    public Employee getEmployee(@PathVariable long user_id){
        return employeeService.getEmployee(user_id);
    }
	
	// update employee rest api
	
	@PutMapping("/employees/{user_id}")
	public Employee createEmployee(@PathVariable long user_id,@RequestBody Employee employee){
        return employeeService.updateEmployee(user_id, employee);
    }
	
	// delete employee rest api
	@DeleteMapping("/employees/{user_id}")
	public void deleteEmployee(@PathVariable long user_id){
		employeeService.deleteEmployee(user_id);
    }
	
	
}
