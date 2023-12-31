package com.example.gipu_android

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AlertDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FoodbankDetailActivity(val getData : FoodBankData, private val context: Context) {

    fun show() {
        val builder = AlertDialog.Builder(context)

        // 다이얼로그의 제목 설정
        builder.setTitle("상세 정보")

        // 다이얼로그의 내용 설정
        getFcltyGrpClscd(getData.name) { fcltydict ->
            val message = buildMessage(fcltydict)
            builder.setMessage(message)

            // 다이얼로그 생성 및 표시
            val dialog = builder.create()
            dialog.show()
        }

        // 확인 버튼 설정
        builder.setPositiveButton("확인") { dialog, _ ->
            dialog.dismiss() // 다이얼로그를 닫음
        }
    }

    private fun buildMessage(fcltydict : MutableMap<String, String>): String {
        val stringBuilder = StringBuilder()
        val useCount_word = "전체 이용자 수 : " + getData.useCount +"\n\n"
        val who = "운영주체 분류 :  " + organizationData.data[getData.who] +"\n\n"
        val whatCenter_word = "시설단체 및 이용건수\n"
        stringBuilder.append(useCount_word)
        stringBuilder.append(who)
        stringBuilder.append(whatCenter_word)

        for ((key, value) in fcltydict) {
            stringBuilder.append("시설 유형 : " + fcltyGrpData.data[key]+"\t\t"+ "이용건수: " + value+"\n")
        }

        return stringBuilder.toString()
    }

    private fun getFcltyGrpClscd(name : String, callback: (MutableMap<String, String>) -> Unit) {
        val service = FcltyGrpInfoImpl.service_ct_tab
        val call = service.requestList(
            serviceKey = SERVICEKEY,
            stdrYm = "202212",
            numOfRows = "10000",
            pageNo = "1",
            dataType = "json",
            spctrCd = name,
        )

        val fcltydict: MutableMap<String, String> = mutableMapOf()

        call.enqueue(object : Callback<fcltyGrpResponse> {
            override fun onResponse(
                call: Call<fcltyGrpResponse>,
                response: Response<fcltyGrpResponse>
            ){
                if (response.isSuccessful){
                    val apiResponse = response.body()
                    Log.d("response", response.toString())
                    if (apiResponse != null){
                        Log.d("apiResponse", apiResponse.toString())

                        val fcltyGrpInfos = apiResponse.response.body.items

                        for (fcltyGrpinfo in fcltyGrpInfos){
                            fcltydict[fcltyGrpinfo.fcltyGrpClscd] = fcltyGrpinfo.useCo
                        }
                        callback(fcltydict)
                    }
                }else{
                    // 서버 응답 실패 처리
                    Log.e("Response", "Response is not successful. Code: ${response.headers()}")
                }
            }
            override fun onFailure(call: Call<fcltyGrpResponse>, t: Throwable) {
                // 네트워크 오류 처리
                Log.e("Response", "Network error: ${t.message}")
            }
        })
    }
}





