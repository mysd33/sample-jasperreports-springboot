package com.example.jaspersample.infra.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.jaspersample.domain.model.Transaction;
import com.example.jaspersample.domain.repository.TransactionsRepository;

@Repository
public class TransactionsRepositoryStub implements TransactionsRepository {

    @Override
    public List<Transaction> findAll() {
        return List.of(
                new Transaction("2020年11月16日", "11時18分26秒", "サイトA", "UNI001TEST - Basic2", "00003427 - PCP 3427",
                        "UNI004AS - Salandy Aaron", "979548", "IDContribuyente01", "E1A1XONM - 000460",
                        "UNI004AS - Salandy Aaron", "Card - 7095030162260000027", "無鉛プラス", 0.775, 2.25, 6.75),
                new Transaction("2020年11月16日", "11時18分26秒", "サイトB", "UNI001TEST - Basic2", "00003427 - PCP 3427",
                        "UNI004AS - Salandy Aaron", "979548", "IDContribuyente01", "E1A1XONM - 000460",
                        "UNI004AS - Salandy Aaron", "Card - 7095030162260000027", "無鉛プラス", 1.225, 3.55, 7.75),
                new Transaction("2020年11月17日", "11時18分26秒", "サイトC", "UNI001TEST - Basic2", "00003427 - PCP 3427",
                        "UNI004AS - JamilXT", "979548", "IDContribuyente02", "E1A1XONM - 000460", "UNI004AS - JamilXT",
                        "Card - 7095030162260000027", "無鉛プラス", 1.775, 4.75, 8.75),
                new Transaction("2020年11月18日", "12時28分26秒", "サイトB", "UNI001TEST - Basic3", "00003427 - PCP 3427",
                        "UNI004AS - Faisal", "979548", "IDContribuyente03", "E1A1XONM - 000460", "UNI004AS - Faisal",
                        "Card - 7095030162260000027", "無鉛プラス", 2.225, 5.95, 6.75),
                new Transaction("2020年11月18日", "12時28分26秒", "サイトB", "UNI001TEST - Basic3", "00003427 - PCP 3427",
                        "UNI004AS - Faisal", "979548", "IDContribuyente03", "E1A1XONM - 000460", "UNI004AS - Faisal",
                        "Card - 7095030162260000027", "無鉛プラス", 2.225, 5.95, 6.75),
                new Transaction("2020年11月18日", "12時28分26秒", "サイトB", "UNI001TEST - Basic3", "00003427 - PCP 3427",
                        "UNI004AS - Faisal", "979548", "IDContribuyente03", "E1A1XONM - 000460", "UNI004AS - Faisal",
                        "Card - 7095030162260000027", "無鉛プラス", 2.225, 5.95, 6.75),
                new Transaction("2020年11月18日", "12時28分26秒", "サイトB", "UNI001TEST - Basic3", "00003427 - PCP 3427",
                        "UNI004AS - Faisal", "979548", "IDContribuyente03", "E1A1XONM - 000460", "UNI004AS - Faisal",
                        "Card - 7095030162260000027", "無鉛プラス", 2.225, 5.95, 6.75),
                new Transaction("2020年11月18日", "12時28分26秒", "サイトB", "UNI001TEST - Basic3", "00003427 - PCP 3427",
                        "UNI004AS - Faisal", "979548", "IDContribuyente03", "E1A1XONM - 000460", "UNI004AS - Faisal",
                        "Card - 7095030162260000027", "無鉛プラス", 2.225, 5.95, 6.75),
                new Transaction("2020年11月18日", "12時28分26秒", "サイトB", "UNI001TEST - Basic3", "00003427 - PCP 3427",
                        "UNI004AS - Faisal", "979548", "IDContribuyente03", "E1A1XONM - 000460", "UNI004AS - Faisal",
                        "Card - 7095030162260000027", "無鉛プラス", 2.225, 5.95, 6.75),
                new Transaction("2020年11月18日", "12時28分26秒", "サイトB", "UNI001TEST - Basic3", "00003427 - PCP 3427",
                        "UNI004AS - Faisal", "979548", "IDContribuyente03", "E1A1XONM - 000460", "UNI004AS - Faisal",
                        "Card - 7095030162260000027", "無鉛プラス", 2.225, 5.95, 6.75),
                new Transaction("2020年11月18日", "12時28分26秒", "サイトB", "UNI001TEST - Basic3", "00003427 - PCP 3427",
                        "UNI004AS - Faisal", "979548", "IDContribuyente03", "E1A1XONM - 000460", "UNI004AS - Faisal",
                        "Card - 7095030162260000027", "無鉛プラス", 2.225, 5.95, 6.75),
                new Transaction("2020年11月18日", "12時28分26秒", "サイトB", "UNI001TEST - Basic3", "00003427 - PCP 3427",
                        "UNI004AS - Faisal", "979548", "IDContribuyente03", "E1A1XONM - 000460", "UNI004AS - Faisal",
                        "Card - 7095030162260000027", "無鉛プラス", 2.225, 5.95, 6.75),
                new Transaction("2020年11月18日", "12時28分26秒", "サイトB", "UNI001TEST - Basic3", "00003427 - PCP 3427",
                        "UNI004AS - Faisal", "979548", "IDContribuyente03", "E1A1XONM - 000460", "UNI004AS - Faisal",
                        "Card - 7095030162260000027", "無鉛プラス", 2.225, 5.95, 6.75));
    }

}
