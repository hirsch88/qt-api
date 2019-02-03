package ch.w3tec.qt.api.application.response;

import lombok.Getter;

import java.util.List;

@Getter
public class PageResponse<T> {

    private List<T> content;
    private Page page;

    private PageResponse(org.springframework.data.domain.Page<T> domainPage) {
        this.content = domainPage.getContent();
        this.page = new Page(domainPage);
    }

    public static <Content> PageResponse<Content> build(org.springframework.data.domain.Page<Content> domainPage) {
        return new PageResponse<>(domainPage);
    }

    @Getter
    private class Page {
        private int size;
        private int totalElements;
        private int totalPages;
        private int number;

        private Page(org.springframework.data.domain.Page domainPage) {
            this.size = domainPage.getSize();
            this.totalElements = domainPage.getTotalPages();
            this.totalPages = domainPage.getTotalPages();
            this.number = domainPage.getNumber();
        }
    }
}
