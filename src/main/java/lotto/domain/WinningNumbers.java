package lotto.domain;

import static java.util.stream.Collectors.*;
import static lotto.domain.BallType.BONUS;
import static lotto.domain.BallType.NORMAL;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WinningNumbers {

    private final List<WinningNumber> winningNumbers;

    private WinningNumbers(List<Integer> normalWinningNumbers, Integer bonusWinningNumber) {
        validateNormalWinningNumbers(normalWinningNumbers);
        validateDuplication(normalWinningNumbers, bonusWinningNumber);

        this.winningNumbers = getWinningNumbers(normalWinningNumbers, bonusWinningNumber);
    }

    public static WinningNumbers create(List<String> normalWinningValues, String bonusWinningValue) {
        List<Integer> normalWinningNumbers = translateIntegerList(normalWinningValues);
        Integer bonusWinningNumber = translateInteger(bonusWinningValue);

        return new WinningNumbers(normalWinningNumbers, bonusWinningNumber);
    }

    public Rank compare(LottoTicket lottoTicket) {
        return Rank.of(getCorrectCount(lottoTicket), isBonus(lottoTicket));
    }

    private static List<Integer> translateIntegerList(List<String> values) {
        try {
            return values.stream()
                    .map(Integer::parseInt)
                    .collect(toList());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("정수여야 합니다. 형식을 확인해주세요.");
        }
    }

    private static Integer translateInteger(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("정수여야 합니다.");
        }
    }

    private List<WinningNumber> getWinningNumbers(List<Integer> normalWinningNumbers, Integer bonusWinningNumber) {
        List<WinningNumber> winningNumbers = new ArrayList<>();

        for (Integer normalWinningNumber : normalWinningNumbers) {
            WinningNumber winningNumber = new WinningNumber(normalWinningNumber, NORMAL);
            winningNumbers.add(winningNumber);
        }

        winningNumbers.add(new WinningNumber(bonusWinningNumber, BONUS));

        return winningNumbers;
    }

    private void validateNormalWinningNumbers(List<Integer> normalWinningNumbers) {
        if (normalWinningNumbers.size() != 6) {
            throw new IllegalArgumentException("당첨 번호는 6개여야 합니다.");
        }
    }

    private void validateDuplication(List<Integer> normalWinningNumbers, Integer bonusWinningNumber) {
        normalWinningNumbers.add(bonusWinningNumber);

        Set<Integer> set = new HashSet<>(normalWinningNumbers);

        if (set.size() < normalWinningNumbers.size()) {
            throw new IllegalArgumentException("중복된 숫자가 존재할 수 없습니다.");
        }

        normalWinningNumbers.remove(bonusWinningNumber);
    }

    private int getCorrectCount(LottoTicket lottoTicket) {
        return winningNumbers.stream()
                .filter(lottoTicket::isSame)
                .collect(toList())
                .size();
    }

    private boolean isBonus(LottoTicket lottoTicket) {
        return winningNumbers.stream()
                .filter(lottoTicket::isSame)
                .anyMatch(WinningNumber::isBonus);
    }
}
