package currencyClientApp;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
    public class CurrencyWebController {

        private final CurrencyService currencyService;

        public CurrencyWebController(CurrencyService currencyService) {
            this.currencyService = currencyService;
        }

        @GetMapping("/convert")
        public String convert(@RequestParam(required = false) String from,
                              @RequestParam(required = false) String to,
                              @RequestParam(required = false) Double amount,
                              Model model) {
            model.addAttribute("currencies", currencyService.getAvailableCurrencies());
            if (from != null && to != null && amount != null) {
                currencyService.convert(from, to, amount).ifPresent(result -> {
                    model.addAttribute("convertedAmount", result);
                    model.addAttribute("from", from);
                    model.addAttribute("to", to);
                    model.addAttribute("amount", amount);
                });
            }
            return "converter"; // converter.html
        }

}
