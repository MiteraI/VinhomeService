package app.vinhomes.common;

import app.vinhomes.entity.Account;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class SessionUserCaller {
    public static Account getSessionUser(HttpServletRequest request) {
        Account acc = null;
        if(request.getSession(false) != null) {
            HttpSession session = request.getSession(false);
            if(session.getAttribute("loginedUser") != null){
                acc = (Account) session.getAttribute("loginedUser");
            }
        }
        return acc;
    }
}
