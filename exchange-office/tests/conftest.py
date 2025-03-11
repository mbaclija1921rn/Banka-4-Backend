import flask
import pytest
import os

from banka4_exchange import create_app

@pytest.fixture(autouse=True)
def disable_remake(monkeypatch):
    monkeypatch.setattr("banka4_exchange.should_remake", lambda: False)

@pytest.fixture()
def app() -> flask.Flask:
    app = create_app()
    app.config["COMMISSION_RATE"] = 0.1
    app.config["EXCHANGERATE_API_KEY"] = "280153fe0016f484aedcecdd"
    project_root = os.path.abspath(os.path.join(os.path.dirname(__file__), ".."))
    app.root_path = project_root
    app.config["EXCHANGE_PATH"] = os.path.join(project_root, "tests", "test_exchanges.json")
    return app
