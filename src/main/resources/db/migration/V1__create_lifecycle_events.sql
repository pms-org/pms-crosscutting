CREATE TABLE lifecycle_events (
    id BIGSERIAL PRIMARY KEY,
    trace_id UUID NOT NULL,
    x_ref VARCHAR(255),
    portfolio_id UUID,
    stage VARCHAR(100),
    status VARCHAR(100),
    timestamp TIMESTAMP NOT NULL,
    details TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_lifecycle_events_trace_id ON lifecycle_events(trace_id);
CREATE INDEX idx_lifecycle_events_portfolio_id ON lifecycle_events(portfolio_id);
CREATE INDEX idx_lifecycle_events_timestamp ON lifecycle_events(timestamp);