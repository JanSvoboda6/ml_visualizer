package com.jan.web.runner;

import com.jan.web.RunRequest;
import com.jan.web.docker.ContainerEntity;
import com.jan.web.docker.ContainerUtility;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@RestController
@RequestMapping("/api/project/runner")
public class ModelRunnerController
{
    private final RunnerRepository runnerRepository;
    private final ContainerUtility containerUtility;
    private final RestTemplate restTemplate;
    private final ModelRunnerService runnerService;
    private final RequestValidator requestValidator;

    @Autowired
    public ModelRunnerController(RunnerRepository runnerRepository,
                                 ContainerUtility containerUtility,
                                 RestTemplate restTemplate,
                                 ModelRunnerService runnerService,
                                 RequestValidator requestValidator)
    {
        this.runnerRepository = runnerRepository;
        this.containerUtility = containerUtility;
        this.restTemplate = restTemplate;
        this.runnerService = runnerService;
        this.requestValidator = requestValidator;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Runner getRunner(@RequestParam long projectId, @RequestParam long runnerId)
    {
        //TODO Jan: Why it has two 2 parameters?
        return runnerRepository.findRunnerByIdAndProjectId(runnerId, projectId);
    }

    @PostMapping("/run")
    public ResponseEntity<?> runProject(@RequestHeader(name = "Authorization") String token, @RequestBody RunRequest request)
    {
        runnerService.runProject(request,
                requestValidator.validateProject(request.getProjectId()),
                requestValidator.validateContainerEntity(containerUtility.getContainerIdFromToken(token)));

        return ResponseEntity.ok().body("Project is running!");
    }

    @GetMapping("/result")
    public ResponseEntity<?> getResult(@RequestHeader(name = "Authorization") String token, @RequestParam long projectId, @RequestParam long runnerId) throws JSONException, IOException
    {

        ContainerEntity containerEntity = requestValidator.validateContainerEntity(containerUtility.getContainerIdFromToken(token));
        requestValidator.validateProject(projectId);
        requestValidator.validateRunner(runnerId);

        JSONObject response = new JSONObject();
        JSONObject resultRequest = new JSONObject();
        ObjectMapper mapper = new ObjectMapper();

        resultRequest.put("projectId", projectId);
        resultRequest.put("runnerId", runnerId);

        HttpHeaders resultHeaders = new HttpHeaders();
        resultHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> resultEntity = new HttpEntity<>(resultRequest.toString(), resultHeaders);
        ResponseEntity<String> resultResponseFromContainer = restTemplate
                .exchange("http://localhost:" + containerEntity.getId() + "/project/runner/result", HttpMethod.POST, resultEntity, String.class);

        ResultResponse resultResponse = mapper.readValue(resultResponseFromContainer.getBody(), ResultResponse.class);

        response.put("firstLabelResult", resultResponse.firstLabelResult);
        response.put("secondLabelResult", resultResponse.secondLabelResult);

        return ResponseEntity.ok(response.toString());
    }
}