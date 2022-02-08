package nextstep.subway.applicaion;

import lombok.val;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationRequest;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Lines;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.domain.object.Distance;
import nextstep.subway.exception.DuplicatedException;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PathService {

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    private final StationService stationService;

    public PathService(LineRepository lineRepository, StationRepository stationRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
        this.stationService = stationService;
    }

    public PathResponse getShortestDistanceAndPath(Long sourceId, Long targetId) {
        val allLines = lineRepository.findAll();
        val sourceStation = stationRepository.getById(sourceId);
        val targetStation = stationRepository.getById(targetId);

        Lines lines = new Lines(allLines);

        val shortestPath = lines.getShortestPath(sourceStation, targetStation);
        val shortestDistance = lines.getShortestPathDistance(sourceStation, targetStation);

        return createResponse(shortestPath, shortestDistance);
    }

    private PathResponse createResponse(List<Station> shortestPath, Distance shortestDistance) {
        List<StationResponse> responses = stationService.createStationResponses(shortestPath);

        return new PathResponse(responses, shortestDistance);
    }
}
