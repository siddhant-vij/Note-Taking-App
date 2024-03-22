from typing import Any
from datetime import datetime


class Note:
    def __init__(self, title: str, content: str, tags: list[str], created_on: datetime, last_updated_on: datetime) -> None:
        self._title = self._validate_type("title", title, str)
        self._content = self._validate_type("content", content, str)
        self._tags = self._validate_type("tags", tags, list)
        self._created_on = self._validate_type(
            "created_on", created_on, datetime)
        self._last_updated_on = self._validate_type(
            "last_updated_on", last_updated_on, datetime)

    @property
    def title(self) -> str:
        return self._title

    @property
    def content(self) -> str:
        return self._content

    @property
    def tags(self) -> list[str]:
        return self._tags

    @property
    def created_on(self) -> datetime:
        return self._created_on

    @property
    def last_updated_on(self) -> datetime:
        return self._last_updated_on

    @staticmethod
    def _validate_type(param_name: str, value: Any, expected_type: type) -> None:
        if not isinstance(value, expected_type):
            raise TypeError(
                f"{param_name} must be a {expected_type}, got {type(value).__name__}")
        return value

    def __str__(self) -> str:
        return f"Note(title={self.title}, content={self.content}, tags={self.tags}, created_on={self.created_on}, last_updated_on={self.last_updated_on})"
