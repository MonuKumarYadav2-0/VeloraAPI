package in.scalive.Velora.dto.response;

import java.util.List;

import org.springframework.data.domain.Page;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PageResponseDTO<T> {
	
	private List<T> content;
	private int pageNumber;
	private int pageSize;
	private long totalElements;
	private int totalPages;
	private boolean first;
	private boolean last;
	private boolean hasNext;
	private boolean hasPrevious;
	
	public static <T> PageResponseDTO<T> fromPage(Page<T> page){
		return PageResponseDTO.<T>builder()
				.content(page.getContent())
				.pageNumber(page.getNumber())
				.pageSize(page.getSize())
				.totalElements(page.getTotalElements())
				.totalPages(page.getTotalPages())
				.first(page.isFirst())
				.last(page.isLast())
				.hasNext(page.hasNext())
				.hasPrevious(page.hasPrevious())
				.build();
	}

}
