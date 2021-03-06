package com.example.shopping

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_cart.*

class Cart : AppCompatActivity() {
    var price : Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        // 삭제여부 한 번 더 확인하기 위한 alert
        var builder = AlertDialog.Builder(this)
        builder.setTitle("장바구니 삭제")
        builder.setMessage("선택한 품목을 장바구니에서 삭제 하시겠습니까?")

        // 장바구니 목록 visibility설정을 위한 변수
        val banana : LinearLayout = findViewById(R.id.banana)
        val apple : LinearLayout = findViewById(R.id.apple)
        val watermelon : LinearLayout = findViewById(R.id.watermelon)

        // firestore이용을 위한 변수
        val db = FirebaseFirestore.getInstance()
        // 장바구니 데이터베이스 collection 주소
        val docRef = db.collection("my").document("cart")

        // 내 장바구니 데이터 베이스에 있는 목록들의 visibility를 visible로 변환
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    if(document.data?.get("banana").toString() != "null"){
                        banana.visibility = View.VISIBLE
                        banana_line.visibility = View.VISIBLE
                    }else{
                        myCart.remove("banana")
                    }
                    if(document.data?.get("apple").toString() != "null"){
                        apple.visibility = View.VISIBLE
                        apple_line.visibility = View.VISIBLE
                    }
                    else{
                        myCart.remove("apple")
                    }
                    if(document.data?.get("watermelon").toString() != "null"){
                        watermelon.visibility = View.VISIBLE
                        wm_line.visibility = View.VISIBLE
                    }
                    else{
                        myCart.remove("watermelon")
                    }
                }
            }

        // 홈으로 버튼 클릭 시 메인화면으로 넘어가기
        cart_to_home.setOnClickListener {
            var intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // 장바구니에서 구매 버튼 클릭 시
        cart_to_buy.setOnClickListener {
            // 아무것도 체크 안하고 누르는걸 검사하기 위한 count변수
            var count : Int = 0
            if(!checkBox_banana.isChecked) count += 1
            if(!checkBox_apple.isChecked) count += 1
            if(!checkBox_wm.isChecked) count += 1

            // 아무것도 체크 안하고 클릭 시 toast메시지 출력
            if(count == 3){
                Toast.makeText(this, "아무것도 선택하지 않았습니다.", Toast.LENGTH_SHORT).show()
            }

            // 하나라도 체크 되어 있으면 실행
            else{
                price = 0
                // 만약 체크 되어 있으면 구매목록 데이터 베이스에 추가
                // 체크 안되어 있으면 구매목록 목록에서 제거해서
                // 결과적으로 체크 한거만 구매목록에 넘어가게 해줌.
                if(checkBox_banana.isChecked){
                    buy_list.put("banana",1500)
                    price += 1500
                }
                else{
                    buy_list.remove("banana")
                }
                if(checkBox_apple.isChecked){
                    buy_list.put("apple",1000)
                    price += 1000
                }
                else{
                    buy_list.remove("apple")
                }
                if(checkBox_wm.isChecked){
                    buy_list.put("watermelon",3000)
                    price += 3000
                }
                else{
                    buy_list.remove("watermelon")
                }
                buy_list.put("price", price)
                // 내 구매목록 데이터베이스에 구매목록이 저장된 buy_list업데이트
                db.collection("m").document("buy")
                    .set(buy_list)
                var intent = Intent(this, Buy::class.java)
                startActivity(intent)
            }
        }

        // 장바구니 삭제
        remove_item.setOnClickListener {
            var count : Int = 0
            if(!checkBox_banana.isChecked) count += 1
            if(!checkBox_apple.isChecked) count += 1
            if(!checkBox_wm.isChecked) count += 1
            if(count == 3){
                Toast.makeText(this,"아무것도 선택하지 않았습니다.",Toast.LENGTH_SHORT).show()
            }else{
                // alert창에서 "네"클릭 시 실행
                builder.setPositiveButton("네") { dialogInterface: DialogInterface, i: Int ->
                    // 만약 체크되어 있으면 데이터베이스에서 해당 품목 삭제 후 db최신화
                    if(checkBox_banana.isChecked){
                        myCart.remove("banana")
                        price -= 1500
                    }else{
                        buy_list.put("banana",3000)
                        price += 1500
                    }
                    if(checkBox_apple.isChecked){
                        myCart.remove("apple")
                        price -= 1000
                    }else{
                        buy_list.put("apple",3000)
                        price += 1000
                    }
                    if(checkBox_wm.isChecked){
                        myCart.remove("watermelon")
                        price -= 3000
                    }else{
                        buy_list.put("watermelon",3000)
                        price += 3000
                    }
                    myCart.put("price", price)
                    // 새로운 myCart목록을 데이터 베이스에 업데이트
                    db.collection("my").document("cart")
                        .set(myCart)
                        .addOnSuccessListener {
                            Toast.makeText(this, "삭제 완료!", Toast.LENGTH_SHORT).show()
                            finish()
                            startActivity(intent)
                        }
                }
                builder.setNeutralButton("아니요",null)
                builder.show()


            }


        }
    }
}