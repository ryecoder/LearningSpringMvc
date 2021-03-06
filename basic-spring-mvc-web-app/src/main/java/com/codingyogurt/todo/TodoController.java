package com.codingyogurt.todo;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

@Controller
public class TodoController {
	
	
	@Autowired 
	TodoService todoService;
	
	@InitBinder
	protected void initBind(WebDataBinder webDataBinder) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		webDataBinder.registerCustomEditor(Date.class, new CustomDateEditor(simpleDateFormat, false));
	}
		
	@RequestMapping(value="/todos", method = RequestMethod.GET)
	public String goToBukasan(ModelMap model){
		
		model.addAttribute("todos", todoService.retrieveTodos(extractLoggedInUserName()));
		return "TodosView";
	}

	private String extractLoggedInUserName() {
		
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		if(principal instanceof UserDetails ) {
			return ((UserDetails) principal).getUsername();
		}
		
		return principal.toString();
	}
	
	@RequestMapping(value = "/addtodo", method = RequestMethod.GET)
	public String goToAddTodo(ModelMap model){
		
		
		model.addAttribute("todoItem", new TodoItem()); 
		
		return "AddTodoView";
		
	}
	
	@RequestMapping(value = "/addtodo", method = RequestMethod.POST)
	public String processAddTodo(ModelMap model, @Valid TodoItem todo, BindingResult result){
		
		if(result.hasErrors()) { 
			return "AddTodoView";
		}
		
		todoService.addTodo(extractLoggedInUserName(), todo.getDesc(), todo.getTargetDate(), false);	
		model.clear();
		return "redirect:/todos";
	}
	
	@RequestMapping(value = "/deletetodo", method = RequestMethod.GET)
	public String deleteTodo(ModelMap model, @RequestParam int id){
		model.clear();
		todoService.deleteTodo(id);
		return "redirect:/todos";
	}
	
	// redirection to update todo
	@RequestMapping(value = "/updatetodo", method = RequestMethod.GET)
	public String updateTodo(ModelMap model, @RequestParam int id){
		model.addAttribute("todoItem", todoService.retrieveTodo(id));
		return "AddTodoView";
	}
	
	// sending post method to /updatetodo url
	@RequestMapping(value = "/updatetodo", method = RequestMethod.POST)
	public String updateTodo(ModelMap model, @Valid TodoItem todoItem, BindingResult result){
		if(result.hasErrors()) {
			return "AddTodoView";
		}
		todoItem.setUser((String) model.get("username"));
		todoService.updateTodo(todoItem);
		
		return "redirect:/todos";
	}
	
	
	
}
