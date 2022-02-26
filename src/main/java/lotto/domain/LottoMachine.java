package lotto.domain;

import lotto.domain.generator.AutoLottoNumberGenerator;
import lotto.domain.vo.LottoPurchaseMoney;

public class LottoMachine {

    public LottoTickets purchase(LottoPurchaseMoney lottoPurchaseMoney) {
        int lottoCount = lottoPurchaseMoney.calculate();

        return new LottoTickets(lottoCount, new AutoLottoNumberGenerator());
    }
}
