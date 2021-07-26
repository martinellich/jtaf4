package ch.jtaf.service;

import ch.jtaf.reporting.data.ClubRankingData;
import ch.jtaf.reporting.data.SeriesRankingData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class SeriesRankingServiceTest {

    @Autowired
    private SeriesRankingService seriesRankingService;

    @Test
    void getClubRanking() {
        ClubRankingData clubRanking = seriesRankingService.getClubRanking(1L);

        assertEquals(4, clubRanking.sortedResults().size());
    }

    @Test
    void getSeriesRanking() {
        SeriesRankingData seriesRanking = seriesRankingService.getSeriesRanking(3L);

        System.out.println(seriesRanking);
    }
}