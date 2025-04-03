package rmartin.ctf.guesser;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Controller
public class WebController {

    private final SuperSecureTokenManager tokenManager;

    @Value("${challenge.flag}")
    String flag;

    public WebController(SuperSecureTokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }

    @GetMapping("/")
    ModelAndView index(ModelAndView mv){
        mv.setViewName("index");
        return mv;
    }

    @GetMapping("/error")
    ModelAndView error(ModelAndView mv){
        mv.setViewName("error");
        return mv;
    }

    @PostMapping("/check")
    ModelAndView checkToken(@RequestParam int number, ModelAndView mv, HttpServletRequest request){
        var session = request.getSession();
        boolean correct = tokenManager.isValidToken(session, number);
        if(correct){
            mv.addObject("flag", flag);
            mv.setViewName("correct");
        } else {
            var usedTokens = tokenManager.getUsedTokens(session);
            mv.addObject("usedTokens", usedTokens);
            mv.setViewName("tryHarder");
        }
        return mv;
    }

    @PostMapping("/reset")
    ModelAndView reset(HttpServletRequest request){
        var session = request.getSession(true);
        tokenManager.resetState(session);
        return new ModelAndView("index");
    }
}
