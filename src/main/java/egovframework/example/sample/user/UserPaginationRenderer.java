package egovframework.example.sample.user;

import egovframework.rte.ptl.mvc.tags.ui.pagination.AbstractPaginationRenderer;

public class UserPaginationRenderer extends AbstractPaginationRenderer {
	public UserPaginationRenderer() {
		
		firstPageLabel = "<a href=\"#\" onclick=\"{0}({1}); return false;\" class=\"link-block-5-copy-2 w-inline-block\"><div class=\"text-block-38\">◀◀</div></a> ";
		previousPageLabel = "<a href=\"#\" onclick=\"{0}({1}); return false;\" class=\"link-block-18 w-inline-block\"><div class=\"text-block-38\">◀</div></a> ";
		currentPageLabel = "<a href=\"#\" onclick=\"{0}({1}); return false;\" class=\"botbtnlink-2 w-inline-block\"><div class=\"botbtntext-2\">{0}</div></a> ";
		otherPageLabel = "<a href=\"#\" onclick=\"{0}({1}); return false;\" class=\"botbtnlink-2 w-inline-block\"><div class=\"botbtntext-2\">{2}</div></a> ";
		nextPageLabel = "<a href=\"#\" onclick=\"{0}({1}); return false;\" class=\"link-block-18 w-inline-block\"><div class=\"text-block-38\">▶</div></a> ";
		lastPageLabel = "<a href=\"#\" onclick=\"{0}({1}); return false;\" class=\"link-block-5-copy-2 w-inline-block\"><div class=\"text-block-38\">▶▶</div></a>";
		
	}
}
