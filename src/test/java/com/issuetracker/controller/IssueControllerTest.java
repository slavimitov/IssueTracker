package com.issuetracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.issuetracker.model.Issue;
import com.issuetracker.model.Issue.IssuePriority;
import com.issuetracker.model.Issue.IssueStatus;
import com.issuetracker.service.IssueService;
import com.issuetracker.service.UserService;
import com.issuetracker.dto.CreateIssueRequest;
import com.issuetracker.dto.IssueDTO;
import com.issuetracker.mapper.IssueCommentMapper;
import com.issuetracker.mapper.IssueMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(IssueController.class)
class IssueControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IssueService issueService;

    @MockBean
    private UserService userService;

    @MockBean
    private IssueMapper issueMapper;
    
    @MockBean
    private IssueCommentMapper commentMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createIssue_ShouldReturnCreated() throws Exception {
        CreateIssueRequest request = new CreateIssueRequest();
        request.setTitle("New Task");
        request.setPriority(IssuePriority.MEDIUM);
        request.setType(Issue.IssueType.TASK);
        request.setProjectId(1L);

        IssueDTO createdIssue = new IssueDTO();
        createdIssue.setId(10L);
        createdIssue.setTitle("New Task");

        when(issueMapper.toEntity(any())).thenReturn(new Issue());
        when(issueService.createIssue(any())).thenReturn(new Issue());
        when(issueMapper.toDTO(any())).thenReturn(createdIssue);

        mockMvc.perform(post("/projects/1/issues")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void startIssue_ShouldReturnOk() throws Exception {
        when(issueService.startIssue(1L)).thenReturn(new Issue());
        when(issueMapper.toDTO(any())).thenReturn(new IssueDTO());

        mockMvc.perform(put("/issues/1/start"))
                .andExpect(status().isOk());
    }
}
