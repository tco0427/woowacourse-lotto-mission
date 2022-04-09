package lotto.controller;

import java.util.ArrayList;
import java.util.List;
import lotto.domain.LottoTicket;
import lotto.domain.vo.LottoNumber;
import lotto.domain.vo.LottoPurchaseMoney;
import lotto.domain.LottoMachine;
import lotto.domain.LottoResult;
import lotto.domain.LottoTickets;
import lotto.domain.WinningNumbers;
import lotto.dto.LottoTicketsDto;
import lotto.view.InputView;
import lotto.view.OutputView;

public class LottoController {

    private final InputView inputView;
    private final OutputView outputView;

    public LottoController(InputView inputView, OutputView outputView) {
        this.inputView = inputView;
        this.outputView = outputView;
    }

    public void run() {
        LottoPurchaseMoney lottoPurchaseMoney = createMoney();

        List<LottoTicket> manualLottoTickets = createManualLottoTicket();

        LottoTickets lottoTickets = createLottoTickets(lottoPurchaseMoney, manualLottoTickets);

        WinningNumbers winningNumbers = createWinningNumbers();

        LottoResult lottoResult = createLottoResult(lottoTickets, winningNumbers);

        outputView.printYield(lottoResult.getRanks(), lottoResult.calculateYield(lottoPurchaseMoney));
    }

    private List<LottoTicket> createManualLottoTicket() {
        int manualCount = inputView.getManualCount();

        List<LottoTicket> manualLottoTickets = new ArrayList<>();

        if (manualCount > 0) {

            setManualLottoTickets(manualCount, manualLottoTickets);
        }

        return manualLottoTickets;
    }

    private void setManualLottoTickets(int manualCount, List<LottoTicket> manualLottoTickets) {
        outputView.printManualLottoGuide();

        for (int i = 0; i < manualCount; i++) {
            List<LottoNumber> manualLottoNumber = inputView.getManualLottoNumber();

            manualLottoTickets.add(new LottoTicket(manualLottoNumber));
        }
    }

    private LottoPurchaseMoney createMoney() {
        try {
            return LottoPurchaseMoney.create(inputView.getMoney());
        } catch(IllegalArgumentException e) {
            outputView.printErrorMessage(e.getMessage());

            return createMoney();
        }
    }

    private LottoTickets createLottoTickets(LottoPurchaseMoney lottoPurchaseMoney, List<LottoTicket> manualLottoTickets) {
        LottoMachine lottoMachine = new LottoMachine();
        LottoTickets lottoTickets = lottoMachine.purchase(lottoPurchaseMoney, manualLottoTickets);
        LottoTicketsDto lottoTicketsDto = new LottoTicketsDto(lottoTickets);

        outputView.printTotalCount(lottoTickets.totalCount());
        outputView.printLottoTicketsInfo(lottoTicketsDto);

        return lottoTickets;
    }

    private WinningNumbers createWinningNumbers() {
        try {
            List<LottoNumber> winningNumbers = inputView.getNormalWinningNumbers();
            LottoNumber bonusNumber = inputView.getBonusNumber();

            return WinningNumbers.create(winningNumbers, bonusNumber);
        } catch (IllegalArgumentException e) {
            outputView.printErrorMessage(e.getMessage());

            return createWinningNumbers();
        }
    }

    private LottoResult createLottoResult(LottoTickets lottoTickets, WinningNumbers winningNumbers) {
        outputView.printLottoResultMessage();

        LottoResult lottoResult = lottoTickets.determine(winningNumbers);

        return lottoResult;
    }
}
