package io.github.merlin.assistant.data.network.service

import io.github.merlin.assistant.data.network.NetworkDataSource
import io.github.merlin.assistant.data.network.response.BasicResponse
import io.github.merlin.assistant.data.network.response.JianGeQueryResponse
import io.github.merlin.assistant.data.network.response.LevelChallengeResponse
import javax.inject.Inject

class JianGeService @Inject constructor(
    private val networkDataSource: NetworkDataSource
) {

    suspend fun query(isSpecial: Int, group: Int = -1): JianGeQueryResponse {
        val params = mutableMapOf<String, Any>()
        params["aid"] = "156"
        params["op"] = "query"
        params["group"] = group
        params["is_special"] = isSpecial
        return networkDataSource.request<JianGeQueryResponse>("activity", params)
    }

    suspend fun levelDetail(levelId: Int): BasicResponse {
        val params = mapOf(
            "aid" to 156,
            "op" to "level_detail",
            "level_id" to levelId,
        )
        return networkDataSource.request<BasicResponse>("activity", params)
    }

    suspend fun levelChallenge(levelId: Int): LevelChallengeResponse {
        val params = mapOf(
            "aid" to 156,
            "op" to "level_challenge",
            "level_id" to levelId,
        )
        return networkDataSource.request<LevelChallengeResponse>("activity", params)
    }

    suspend fun passLevelAwardGet(costDouyu: Int, index: Int): BasicResponse {
        val params = mapOf(
            "aid" to 156,
            "op" to "pass_level_award_get",
            "cost_douyu" to costDouyu,
            "index" to index,
        )
        return networkDataSource.request<BasicResponse>("activity", params)
    }

    suspend fun passLevelAwardClose(): BasicResponse {
        val params = mapOf(
            "aid" to 156,
            "op" to "pass_level_award_close",
        )
        return networkDataSource.request<BasicResponse>("activity", params)
    }

    suspend fun getAllRoleAward(): BasicResponse {
        val params = mapOf(
            "aid" to 156,
            "op" to "get_all_role_award",
        )
        return networkDataSource.request<BasicResponse>("activity", params)
    }

    suspend fun getAllFactionAward(): BasicResponse {
        val params = mapOf(
            "aid" to 156,
            "op" to "get_all_faction_award",
        )
        return networkDataSource.request<BasicResponse>("activity", params)
    }

}