package com.jan.web.runner;

import com.jan.web.docker.ContainerEntity;
import com.jan.web.docker.ContainerRepository;
import com.jan.web.docker.ContainerUtility;
import com.jan.web.request.RequestMaker;
import com.jan.web.request.RequestMethod;
import com.jan.web.result.Result;
import com.jan.web.result.ResultRepository;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/api/project/runner")
public class RunnerController
{
    private final RunnerRepository runnerRepository;
    private final ContainerUtility containerUtility;
    private final RunnerService runnerService;
    private final RequestValidator requestValidator;
    private final RequestMaker requestMaker;
    private final ObjectMapper objectMapper;
    private final ResultRepository resultRepository;

    @Autowired
    public RunnerController(RunnerRepository runnerRepository,
                            ContainerUtility containerUtility,
                            RunnerService runnerService,
                            RequestValidator requestValidator,
                            RequestMaker requestMaker,
                            ObjectMapper objectMapper, ResultRepository resultRepository)
    {
        this.runnerRepository = runnerRepository;
        this.containerUtility = containerUtility;
        this.runnerService = runnerService;
        this.requestValidator = requestValidator;
        this.requestMaker = requestMaker;
        this.objectMapper = objectMapper;
        this.resultRepository = resultRepository;
    }


    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Runner getRunner(@RequestParam long projectId, @RequestParam long runnerId)
    {
        //TODO Jan: Why it has two 2 parameters? - we need just one parameter
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

    //TODO: Jan - projectId is redundant
    @GetMapping("/result")
    public ResponseEntity<?> getResult(@RequestHeader(name = "Authorization") String token, @RequestParam long projectId, @RequestParam long runnerId) throws JSONException, IOException
    {
        ContainerEntity containerEntity = requestValidator.validateContainerEntity(containerUtility.getContainerIdFromToken(token));
        requestValidator.validateProject(projectId);
        requestValidator.validateRunner(runnerId);

        Optional<Result> result = runnerService.getResult(containerEntity.getId(), projectId, runnerId);
        if(result.isPresent())
        {
            return ResponseEntity.ok(prepareJsonResultResponse(result.get()));
        }
        return ResponseEntity.ok("Result cannot be obtained since project is still running!");
    }

    @PostMapping(value = "/finished", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> isFinished(@RequestHeader(name="Authorization") String token, @RequestBody FinishedRequest finishedRequest) throws JSONException, IOException
    {
        ContainerEntity containerEntity = requestValidator.validateContainerEntity(containerUtility.getContainerIdFromToken(token));
        requestValidator.validateProject(finishedRequest.getProjectId());
        requestValidator.validateRunner(finishedRequest.getRunnerId());

        JSONObject response = new JSONObject();
        response.put("isFinished", runnerService.isFinished(containerEntity.getId(), finishedRequest.getProjectId(), finishedRequest.getRunnerId()));

        return ResponseEntity.ok(response.toString());
    }

    private JSONObject prepareJsonResultResponse(Result result) throws JSONException
    {
        JSONObject response = new JSONObject();
        response.put("firstLabelResult", result.getFirstLabelResult());
        response.put("secondLabelResult", result.getSecondLabelResult());
        return response;
    }
}