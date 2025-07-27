FROM python:3.12-slim

WORKDIR /app

# Create a new user so we are not running as root
RUN useradd --create-home --shell /bin/bash sandboxuser
USER sandboxuser

ENTRYPOINT ["python"]

CMD ["/app/scripts/script.py"]