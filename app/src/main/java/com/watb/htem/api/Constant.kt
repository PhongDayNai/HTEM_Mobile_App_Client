package com.watb.htem.api

class Constant {
    companion object {
        var Main_Url = ""
        val Login_Url: String
            get() = Main_Url + "api/users/login"
        val Register_Url: String
            get() = Main_Url + "api/users/register"
        val Forgot_Password_Url: String
            get() = Main_Url + "api/users/forgot-password"
        val Change_Password_Url: String
            get() = Main_Url + "api/users/change-password"
        val User_Points_Url: String
            get() = Main_Url + "api/users/points"
        val User_Transaction_Url: String
            get() = Main_Url + "api/users/get-transaction"
        val Call_Staff_Url: String
            get() = Main_Url + "api/users/call-staff"

        val Guess_Buffet_Url: String
            get() = Main_Url + "api/buffets/guess"
        val User_Buffet_Url: String
            get() = Main_Url + "api/buffets/user"

        val Add_Dishes_Url: String
            get() = Main_Url + "api/orders/add-dishes"
        val Get_Served_Dishes_Url: String
            get() = Main_Url + "api/orders/get-served"

        val Payment_User_Url: String
            get() = Main_Url + "api/payments/handleUser"
        val Payment_Guess_Url: String
            get() = Main_Url + "api/payments/handleGuess"
        val Check_Payment_State_Url: String
            get() = Main_Url + "api/payments/state"

        fun setMainUrl(url: String) {
            Main_Url = "https://${url.trim()}.loca.lt/" // Phai co / o cuoi
        }
    }
}
