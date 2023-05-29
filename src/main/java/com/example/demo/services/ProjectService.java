package com.example.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.domain.Backlog;
import com.example.demo.domain.Project;
import com.example.demo.domain.User;
import com.example.demo.exceptions.ProjectIdException;
import com.example.demo.exceptions.ProjectNotFoundException;
import com.example.demo.repositories.BacklogRepository;
import com.example.demo.repositories.ProjectRepository;
import com.example.demo.repositories.UserRepository;

@Service
public class ProjectService {
	@Autowired
	private ProjectRepository projectRepository;
	@Autowired
	private BacklogRepository backlogRepository;
	@Autowired
	private UserRepository userRepository;
	public Project saveOrUpdateProject(Project project, String username) {
		if(project.getId() != null) {
			Project existingProject = projectRepository.findByProjectIdentifier(project.getProjectIdentifier());	
			if(existingProject != null && (!existingProject.getProjectLeader().equals(username))) {
				throw new ProjectNotFoundException("Project not found in your account");
			}else if (existingProject == null) {
				throw new ProjectNotFoundException("Project with ID: '"+project.getProjectIdentifier()+"' cannot be updated because it doesn't exist");
			}
		}
		try {
			User user = userRepository.findByUsername(username);
			project.setUser(user);
			project.setProjectLeader(user.getUsername());
			project.setProjectIdentifier(project.getProjectIdentifier().toUpperCase());
			
			if(project.getId() == null) {
				Backlog backlog = new Backlog();
				project.setBacklog(backlog);
				backlog.setProject(project);
				backlog.setProjectIdentifier(project.getProjectIdentifier().toUpperCase());
			}
			if(project.getId() != null) {
				project.setBacklog(backlogRepository.findByProjectIdentifier(project.getProjectIdentifier().toUpperCase()));
			}
			return projectRepository.save(project);
			
		}catch(Exception ex) {
			throw new ProjectIdException("Project ID '"+project.getProjectIdentifier().toUpperCase()+"' already exists"); 
			
		}		
	}
	
	public Project findProjectByIdentifier(String projectId, String username) {
		Project project  = projectRepository.findByProjectIdentifier(projectId.toUpperCase());
		if(project == null) {
			throw new ProjectIdException("Project ID '"+projectId+"' does not exist");			
		}
		if(!project.getProjectLeader().equals(username)) {
			throw new ProjectNotFoundException("Project not found in your account");				
		}
		if(!project.getProjectIdentifier().equals(projectId)) {
			throw new ProjectNotFoundException("ProjectId not found in your account");	
		}
		return project;
	}
	public Iterable<Project> findAllProjects(){
		return projectRepository.findAll();	
	}
	public void deleteProjectByIdentifier(String projectId, String username) {
//		Project project =  projectRepository.findByProjectIdentifier(projectId.toUpperCase());
//		
//		if(project == null) {
//			throw new ProjectIdException("Cannot Project with ID '"+projectId+"'. This project does not exist");		
//		}
//		projectRepository.delete(project);
		
		projectRepository.delete(findProjectByIdentifier(projectId, username));
	}
//	public Project updateProject(Project project) {
//		try {
//			project.setId(project.getId());
//			return projectRepository.save(project);
//		}catch(Exception ex) {
//			throw new ProjectIdException("ID '"+project.getId()+"' update success"); 
//			
//		}	
//	}
}
