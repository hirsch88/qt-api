package ch.w3tec.qt.api.domain.service.planing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ParingTableService {

    @Autowired
    public ParingTableService() {
    }

    public ParingTable generate(ParingTableGenerationOptions paringTableGenerationOptions) {
        ParingTableGenerator paringTableGenerator = ParingTableGenerator.getInstance();
        return paringTableGenerator
                .withNumberOfTeams(paringTableGenerationOptions.getNumberOfTeams())
                .generate();
    }

}
