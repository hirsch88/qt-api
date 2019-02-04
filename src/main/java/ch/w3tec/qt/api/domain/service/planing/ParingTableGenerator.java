package ch.w3tec.qt.api.domain.service.planing;

class ParingTableGenerator {

    static ParingTable generate(int numberOfTeams) {
        ParingTable paringTable = new ParingTable(numberOfTeams);
        return paringTable.fillOutTable();
    }

}
