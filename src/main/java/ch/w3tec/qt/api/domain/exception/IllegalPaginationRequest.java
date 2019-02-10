package ch.w3tec.qt.api.domain.exception;

import lombok.Getter;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IllegalPaginationRequest extends RuntimeException {

    public IllegalPaginationRequest(Pageable pageRequest) {
        super("This is a invalid pagination(pageNumber=" + pageRequest.getPageNumber() + ", pageSize=" + pageRequest.getPageSize() + ") or sort=" + pageRequest.getSort().toString() + " request");
    }

}
